package io.github.jlmc.maydayhlsprovider.application;

import io.github.jlmc.maydayhlsprovider.application.port.in.SearchEncountersUseCase;
import io.github.jlmc.maydayhlsprovider.application.port.out.AccountConcurrencyPolicy;
import io.github.jlmc.maydayhlsprovider.application.port.out.ProviderEncounterSearchPort;
import io.github.jlmc.maydayhlsprovider.domain.AccountId;
import io.github.jlmc.maydayhlsprovider.domain.EncounterResult;
import io.github.jlmc.maydayhlsprovider.domain.TimeWindow;
import io.github.jlmc.maydayhlsprovider.domain.TimeWindowSplitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Application Core. No Spring, no WebClient, no HTTP — depends only on ports and domain types.
 *
 * <p>Semantics: the first matching result to arrive wins (not the chronologically earliest
 * encounter). {@code .next()} on the merged {@link Flux} triggers Reactor's native cancellation:
 * windows not yet subscribed are never started, and in-flight windows (including any pending
 * retries/delays in the outbound adapter) are cancelled. No manual cancellation bookkeeping.</p>
 */
public final class SearchEncountersService implements SearchEncountersUseCase {

    private static final Logger log = LoggerFactory.getLogger(SearchEncountersService.class);

    private final TimeWindowSplitter splitter;
    private final ProviderEncounterSearchPort searchPort;
    private final AccountConcurrencyPolicy concurrencyPolicy;
    private final Duration windowSize;

    public SearchEncountersService(TimeWindowSplitter splitter,
                                    ProviderEncounterSearchPort searchPort,
                                    AccountConcurrencyPolicy concurrencyPolicy,
                                    Duration windowSize) {
        this.splitter = Objects.requireNonNull(splitter);
        this.searchPort = Objects.requireNonNull(searchPort);
        this.concurrencyPolicy = Objects.requireNonNull(concurrencyPolicy);
        this.windowSize = Objects.requireNonNull(windowSize);
    }

    @Override
    public Mono<EncounterResult> search(AccountId accountId, Instant startDate, Instant endDate, String visitType) {
        List<TimeWindow> windows = splitter.split(startDate, endDate, windowSize);
        if (windows.isEmpty()) {
            return Mono.empty();
        }

        int maxConcurrency = concurrencyPolicy.maxConcurrency(accountId);
        log.info("Searching provider accountId={} windows={} maxConcurrency={} visitType={}",
                accountId.value(), windows.size(), maxConcurrency, visitType);

        return Flux.fromIterable(windows)
                .flatMap(window -> searchWindow(accountId, window, visitType), maxConcurrency)
                .next();
    }

    private Mono<EncounterResult> searchWindow(AccountId accountId, TimeWindow window, String visitType) {
        log.debug("Searching provider window accountId={} start={} end={}",
                accountId.value(), window.start(), window.end());
        return searchPort.search(accountId, window, visitType)
                .filter(encounter -> encounter.visitType().equals(visitType))
                .next()
                .map(EncounterResult::from)
                .doOnNext(result -> log.info("Encounter found accountId={} encounterId={}",
                        accountId.value(), result.encounterId()))
                .doOnCancel(() -> log.debug("Cancelled window accountId={} start={} end={}",
                        accountId.value(), window.start(), window.end()));
    }
}
