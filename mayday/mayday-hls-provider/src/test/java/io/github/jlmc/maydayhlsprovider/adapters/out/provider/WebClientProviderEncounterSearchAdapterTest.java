package io.github.jlmc.maydayhlsprovider.adapters.out.provider;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import io.github.jlmc.maydayhlsprovider.application.exception.ProviderClientErrorException;
import io.github.jlmc.maydayhlsprovider.application.exception.ProviderRetriesExhaustedException;
import io.github.jlmc.maydayhlsprovider.configuration.properties.ProviderProperties;
import io.github.jlmc.maydayhlsprovider.domain.AccountId;
import io.github.jlmc.maydayhlsprovider.domain.TimeWindow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.Instant;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Adapter/integration tests against a real (in-process) WireMock instance. Retry internals
 * (429/5xx/exhaustion/no-retry-on-other-4xx) live here, not in {@code SearchEncountersServiceTest},
 * because that's where the retry composition actually is.
 */
class WebClientProviderEncounterSearchAdapterTest {

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    private static final AccountId ACCOUNT = new AccountId("account-123");
    private static final TimeWindow WINDOW = new TimeWindow(
            Instant.parse("2026-09-05T00:00:00Z"), Instant.parse("2026-09-06T00:00:00Z"));
    private static final String VISIT_TYPE = "FOLLOW_UP";

    private WebClientProviderEncounterSearchAdapter adapterWith(ProviderProperties.RetryProperties retryProperties) {
        WebClient webClient = WebClient.builder().baseUrl(wireMock.baseUrl()).build();
        return new WebClientProviderEncounterSearchAdapter(webClient, retryProperties, Schedulers.parallel());
    }

    private static ProviderProperties.RetryProperties fastRetry(int maxAttempts) {
        return new ProviderProperties.RetryProperties(maxAttempts,
                new ProviderProperties.RetryProperties.Backoff(Duration.ofMillis(20), Duration.ofMillis(200), 0.2));
    }

    @Test
    void sendsExpectedQueryParamsAndAccountHeader() {
        wireMock.stubFor(get(urlPathEqualTo("/provider/encounters"))
                .withQueryParam("startDate", equalTo("2026-09-05T00:00:00Z"))
                .withQueryParam("endDate", equalTo("2026-09-06T00:00:00Z"))
                .withQueryParam("visitType", equalTo(VISIT_TYPE))
                .withHeader("X-Account-Id", equalTo("account-123"))
                .willReturn(okJson("{\"encounters\":[]}")));

        StepVerifier.create(adapterWith(fastRetry(3)).search(ACCOUNT, WINDOW, VISIT_TYPE))
                .verifyComplete();

        wireMock.verify(1, getRequestedFor(urlPathEqualTo("/provider/encounters")));
    }

    @Test
    void matchingWindow_parsesEncounters() {
        wireMock.stubFor(get(urlPathEqualTo("/provider/encounters")).willReturn(okJson(
                """
                {"encounters":[{"id":"encounter-123","visitType":"FOLLOW_UP","date":"2026-09-05T10:00:00Z","durationSeconds":1800}]}
                """)));

        StepVerifier.create(adapterWith(fastRetry(3)).search(ACCOUNT, WINDOW, VISIT_TYPE))
                .assertNext(encounter -> {
                    assertThat(encounter.id()).isEqualTo("encounter-123");
                    assertThat(encounter.visitType()).isEqualTo("FOLLOW_UP");
                    assertThat(encounter.durationSeconds()).isEqualTo(1800);
                })
                .verifyComplete();
    }

    @Test
    void emptyWindow_completesWithoutEncounters() {
        wireMock.stubFor(get(urlPathEqualTo("/provider/encounters")).willReturn(okJson("{\"encounters\":[]}")));

        StepVerifier.create(adapterWith(fastRetry(3)).search(ACCOUNT, WINDOW, VISIT_TYPE))
                .verifyComplete();
    }

    @Test
    void rateLimited_retriesUsingRetryAfterThenSucceeds() {
        wireMock.stubFor(get(urlPathEqualTo("/provider/encounters")).inScenario("retry-then-success")
                .whenScenarioStateIs(STARTED)
                .willSetStateTo("RETRIED")
                .willReturn(aResponse().withStatus(429).withHeader("Retry-After", "1")));
        wireMock.stubFor(get(urlPathEqualTo("/provider/encounters")).inScenario("retry-then-success")
                .whenScenarioStateIs("RETRIED")
                .willReturn(okJson("{\"encounters\":[]}")));

        StepVerifier.create(adapterWith(fastRetry(3)).search(ACCOUNT, WINDOW, VISIT_TYPE))
                .verifyComplete();

        wireMock.verify(2, getRequestedFor(urlPathEqualTo("/provider/encounters")));
    }

    @Test
    void rateLimitedRepeatedly_exhaustsRetriesAndPropagates() {
        wireMock.stubFor(get(urlPathEqualTo("/provider/encounters"))
                .willReturn(aResponse().withStatus(429).withHeader("Retry-After", "0")));

        StepVerifier.create(adapterWith(fastRetry(3)).search(ACCOUNT, WINDOW, VISIT_TYPE))
                .expectErrorMatches(ProviderRetriesExhaustedException.class::isInstance)
                .verify(Duration.ofSeconds(5));

        wireMock.verify(3, getRequestedFor(urlPathEqualTo("/provider/encounters")));
    }

    @Test
    void serverErrorWithBackoffJitter_retriesBoundedThenSucceeds() {
        wireMock.stubFor(get(urlPathEqualTo("/provider/encounters")).inScenario("5xx-then-success")
                .whenScenarioStateIs(STARTED)
                .willSetStateTo("RECOVERED")
                .willReturn(aResponse().withStatus(503)));
        wireMock.stubFor(get(urlPathEqualTo("/provider/encounters")).inScenario("5xx-then-success")
                .whenScenarioStateIs("RECOVERED")
                .willReturn(okJson("{\"encounters\":[]}")));

        StepVerifier.create(adapterWith(fastRetry(3)).search(ACCOUNT, WINDOW, VISIT_TYPE))
                .verifyComplete();

        wireMock.verify(2, getRequestedFor(urlPathEqualTo("/provider/encounters")));
    }

    @Test
    void otherFourXX_doesNotRetry_propagatesImmediately() {
        wireMock.stubFor(get(urlPathEqualTo("/provider/encounters")).willReturn(aResponse().withStatus(400)));

        StepVerifier.create(adapterWith(fastRetry(3)).search(ACCOUNT, WINDOW, VISIT_TYPE))
                .expectErrorMatches(ProviderClientErrorException.class::isInstance)
                .verify(Duration.ofSeconds(2));

        wireMock.verify(1, getRequestedFor(urlPathEqualTo("/provider/encounters")));
    }

    @Test
    void rateLimitIsIndependentPerAccount() {
        wireMock.stubFor(get(urlPathEqualTo("/provider/encounters"))
                .withHeader("X-Account-Id", equalTo("account-A"))
                .inScenario("account-A-limit")
                .whenScenarioStateIs(STARTED)
                .willSetStateTo("A_LIMITED")
                .willReturn(okJson("{\"encounters\":[]}")));
        wireMock.stubFor(get(urlPathEqualTo("/provider/encounters"))
                .withHeader("X-Account-Id", equalTo("account-A"))
                .inScenario("account-A-limit")
                .whenScenarioStateIs("A_LIMITED")
                .willReturn(aResponse().withStatus(429).withHeader("Retry-After", "0")));
        wireMock.stubFor(get(urlPathEqualTo("/provider/encounters"))
                .withHeader("X-Account-Id", equalTo("account-B"))
                .willReturn(okJson("{\"encounters\":[]}")));

        WebClientProviderEncounterSearchAdapter adapter = adapterWith(fastRetry(2));

        // account-A: first search succeeds and flips the scenario into its limited state.
        StepVerifier.create(adapter.search(new AccountId("account-A"), WINDOW, VISIT_TYPE)).verifyComplete();
        // account-A: second search now exhausts retries against the still-limited scenario.
        StepVerifier.create(adapter.search(new AccountId("account-A"), WINDOW, VISIT_TYPE))
                .expectErrorMatches(ProviderRetriesExhaustedException.class::isInstance)
                .verify(Duration.ofSeconds(2));

        // account-B is entirely unaffected by account-A's rate limit state.
        StepVerifier.create(adapter.search(new AccountId("account-B"), WINDOW, VISIT_TYPE)).verifyComplete();
    }
}
