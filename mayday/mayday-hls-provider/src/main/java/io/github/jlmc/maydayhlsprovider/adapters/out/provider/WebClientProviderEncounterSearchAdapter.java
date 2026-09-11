package io.github.jlmc.maydayhlsprovider.adapters.out.provider;

import io.github.jlmc.maydayhlsprovider.adapters.out.provider.dto.ProviderEncounterDto;
import io.github.jlmc.maydayhlsprovider.adapters.out.provider.dto.ProviderEncountersResponse;
import io.github.jlmc.maydayhlsprovider.application.exception.ProviderClientErrorException;
import io.github.jlmc.maydayhlsprovider.application.exception.ProviderRateLimitedException;
import io.github.jlmc.maydayhlsprovider.application.exception.ProviderRetriesExhaustedException;
import io.github.jlmc.maydayhlsprovider.application.exception.ProviderServerErrorException;
import io.github.jlmc.maydayhlsprovider.application.port.out.ProviderEncounterSearchPort;
import io.github.jlmc.maydayhlsprovider.configuration.properties.ProviderProperties;
import io.github.jlmc.maydayhlsprovider.domain.AccountId;
import io.github.jlmc.maydayhlsprovider.domain.Encounter;
import io.github.jlmc.maydayhlsprovider.domain.TimeWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Outbound adapter: encapsulates WebClient, URL, query params, headers, status handling and
 * response parsing. The Application Core never sees any of this — only {@link Encounter}
 * and typed exceptions.
 *
 * <p>Retry is composed here (not in the use case) with plain Reactor {@link Retry#from}, not
 * Resilience4j: the 429 delay is dynamic per attempt (the provider's own {@code Retry-After}),
 * which {@code Retry.from} receives directly via {@code RetrySignal.failure()} — Resilience4j's
 * {@code IntervalFunction} is attempt-count-based and would need a mutable side-channel to carry
 * a response-supplied delay, which is exactly the manual bookkeeping this design avoids.</p>
 */
public final class WebClientProviderEncounterSearchAdapter implements ProviderEncounterSearchPort {

    private static final Logger log = LoggerFactory.getLogger(WebClientProviderEncounterSearchAdapter.class);

    private final WebClient webClient;
    private final ProviderProperties.RetryProperties retryProperties;
    private final Scheduler delayScheduler;

    public WebClientProviderEncounterSearchAdapter(WebClient webClient, ProviderProperties.RetryProperties retryProperties) {
        this(webClient, retryProperties, Schedulers.parallel());
    }

    WebClientProviderEncounterSearchAdapter(WebClient webClient,
                                             ProviderProperties.RetryProperties retryProperties,
                                             Scheduler delayScheduler) {
        this.webClient = Objects.requireNonNull(webClient);
        this.retryProperties = Objects.requireNonNull(retryProperties);
        this.delayScheduler = Objects.requireNonNull(delayScheduler);
    }

    @Override
    public Flux<Encounter> search(AccountId accountId, TimeWindow window, String visitType) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/provider/encounters")
                        .queryParam("startDate", window.start())
                        .queryParam("endDate", window.end())
                        .queryParam("visitType", visitType)
                        .build())
                .header("X-Account-Id", accountId.value())
                .exchangeToMono(response -> toOutcomeOrError(accountId, window, response))
                .retryWhen(retrySpec(accountId, window))
                .flatMapMany(success -> Flux.fromIterable(success.encounters()));
    }

    private Mono<ProviderCallOutcome.Success> toOutcomeOrError(AccountId accountId, TimeWindow window, ClientResponse response) {
        return toOutcome(accountId, window, response).flatMap(outcome -> switch (outcome) {
            case ProviderCallOutcome.Success success -> Mono.just(success);
            case ProviderCallOutcome.RateLimited rateLimited -> Mono.error(new ProviderRateLimitedException(rateLimited.retryAfter()));
            case ProviderCallOutcome.ServerError serverError -> Mono.error(new ProviderServerErrorException(serverError.status()));
            case ProviderCallOutcome.ClientError clientError -> Mono.error(new ProviderClientErrorException(clientError.status()));
        });
    }

    private Mono<ProviderCallOutcome> toOutcome(AccountId accountId, TimeWindow window, ClientResponse response) {
        HttpStatusCode status = response.statusCode();
        if (status.is2xxSuccessful()) {
            return response.bodyToMono(ProviderEncountersResponse.class)
                    .map(body -> new ProviderCallOutcome.Success(body.encounters().stream().map(ProviderEncounterDto::toDomain).toList()));
        }
        if (status.value() == 429) {
            Duration retryAfter = RetryAfterHeaderParser.parse(response.headers().asHttpHeaders().getFirst(HttpHeaders.RETRY_AFTER)).orElse(null);
            log.warn("Provider rate limited request accountId={} start={} end={} retryAfter={}",
                    accountId.value(), window.start(), window.end(), retryAfter);
            return response.releaseBody().thenReturn(new ProviderCallOutcome.RateLimited(retryAfter));
        }
        if (status.is5xxServerError()) {
            log.warn("Provider server error accountId={} start={} end={} status={}",
                    accountId.value(), window.start(), window.end(), status.value());
            return response.releaseBody().thenReturn(new ProviderCallOutcome.ServerError(status.value()));
        }
        log.warn("Provider client error accountId={} start={} end={} status={}",
                accountId.value(), window.start(), window.end(), status.value());
        return response.releaseBody().thenReturn(new ProviderCallOutcome.ClientError(status.value()));
    }

    private Retry retrySpec(AccountId accountId, TimeWindow window) {
        int maxAttempts = retryProperties.maxAttempts();
        ProviderProperties.RetryProperties.Backoff backoff = retryProperties.backoff();

        return Retry.from(companion -> companion.flatMap(signal -> {
            // attemptsMade counts HTTP calls already made, including the one that just failed.
            // max-attempts is the total attempt budget (initial call + retries), so we retry
            // only while attemptsMade < maxAttempts -- never infinitely.
            long attemptsMade = signal.totalRetries() + 1;
            return switch (signal.failure()) {
                case ProviderRateLimitedException e when attemptsMade < maxAttempts -> {
                    Duration delay = e.retryAfter() != null ? e.retryAfter() : jitteredBackoff(attemptsMade, backoff);
                    log.info("Retrying after rate limit accountId={} start={} end={} attemptsMade={} delay={}",
                            accountId.value(), window.start(), window.end(), attemptsMade, delay);
                    yield Mono.delay(delay, delayScheduler);
                }
                case ProviderServerErrorException e when attemptsMade < maxAttempts -> {
                    Duration delay = jitteredBackoff(attemptsMade, backoff);
                    log.info("Retrying after server error accountId={} start={} end={} attemptsMade={} delay={}",
                            accountId.value(), window.start(), window.end(), attemptsMade, delay);
                    yield Mono.delay(delay, delayScheduler);
                }
                case ProviderRateLimitedException e -> Mono.error(new ProviderRetriesExhaustedException(e));
                case ProviderServerErrorException e -> Mono.error(new ProviderRetriesExhaustedException(e));
                // ProviderClientErrorException and anything else: no retry, propagate as-is.
                default -> Mono.error(signal.failure());
            };
        }));
    }

    private static Duration jitteredBackoff(long attemptsMade, ProviderProperties.RetryProperties.Backoff backoff) {
        double base = backoff.minBackoff().toMillis() * Math.pow(2, attemptsMade - 1);
        double capped = Math.min(base, backoff.maxBackoff().toMillis());
        double jitterRange = capped * backoff.jitterFactor();
        if (jitterRange <= 0) {
            return Duration.ofMillis(Math.round(capped));
        }
        double low = Math.max(0, capped - jitterRange);
        double high = capped + jitterRange;
        double jittered = ThreadLocalRandom.current().nextDouble(low, high);
        return Duration.ofMillis(Math.round(jittered));
    }
}
