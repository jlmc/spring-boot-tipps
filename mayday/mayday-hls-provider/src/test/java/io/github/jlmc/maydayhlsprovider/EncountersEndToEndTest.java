package io.github.jlmc.maydayhlsprovider;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

/**
 * Full-context regression covering the Definition of Done curl flow end-to-end: real HTTP
 * inbound adapter, real use case, real outbound WebClient adapter, against an in-process
 * WireMock standing in for the provider.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class EncountersEndToEndTest {

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    static void providerBaseUrl(DynamicPropertyRegistry registry) {
        registry.add("mayday.provider.base-url", wireMock::baseUrl);
    }

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void matchingSearch_returnsFoundEncounter() {
        // Explicit priority: without it, WireMock does not reliably prefer the more specific,
        // earlier-registered stub over a broader one added later.
        wireMock.stubFor(get(urlPathEqualTo("/provider/encounters"))
                .atPriority(1)
                .withQueryParam("startDate", equalTo("2026-09-05T00:00:00Z"))
                .withQueryParam("endDate", equalTo("2026-09-06T00:00:00Z"))
                .willReturn(okJson(
                        """
                        {"encounters":[{"id":"encounter-123","visitType":"FOLLOW_UP","date":"2026-09-05T10:00:00Z","durationSeconds":1800}]}
                        """)));
        wireMock.stubFor(get(urlPathEqualTo("/provider/encounters")).atPriority(10).willReturn(okJson("{\"encounters\":[]}")));

        webTestClient.get()
                .uri("/encounters?startDate=2026-09-01T00:00:00Z&endDate=2026-09-10T00:00:00Z&visitType=FOLLOW_UP")
                .header("Authorization", "Bearer account-123")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.found").isEqualTo(true)
                .jsonPath("$.encounterId").isEqualTo("encounter-123")
                .jsonPath("$.durationSeconds").isEqualTo(1800);
    }

    @Test
    void noMatch_returns200WithFoundFalse() {
        wireMock.stubFor(get(urlPathEqualTo("/provider/encounters")).willReturn(okJson("{\"encounters\":[]}")));

        webTestClient.get()
                .uri("/encounters?startDate=2026-09-01T00:00:00Z&endDate=2026-09-03T00:00:00Z&visitType=FOLLOW_UP")
                .header("Authorization", "Bearer account-123")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.found").isEqualTo(false);
    }

    @Test
    void missingAuthorization_returns401() {
        webTestClient.get()
                .uri("/encounters?startDate=2026-09-01T00:00:00Z&endDate=2026-09-03T00:00:00Z&visitType=FOLLOW_UP")
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
