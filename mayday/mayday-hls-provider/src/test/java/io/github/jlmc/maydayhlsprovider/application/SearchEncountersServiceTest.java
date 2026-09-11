package io.github.jlmc.maydayhlsprovider.application;

import io.github.jlmc.maydayhlsprovider.application.port.out.AccountConcurrencyPolicy;
import io.github.jlmc.maydayhlsprovider.application.port.out.ProviderEncounterSearchPort;
import io.github.jlmc.maydayhlsprovider.domain.AccountId;
import io.github.jlmc.maydayhlsprovider.domain.Encounter;
import io.github.jlmc.maydayhlsprovider.domain.FixedSizeTimeWindowSplitter;
import io.github.jlmc.maydayhlsprovider.domain.TimeWindow;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Pure use-case tests: fake {@link ProviderEncounterSearchPort}, no Spring, no WireMock.
 * Retry internals (429/5xx/exhaustion) are adapter-level concerns, tested against the real
 * {@code WebClientProviderEncounterSearchAdapter} instead — see that adapter's test class.
 */
class SearchEncountersServiceTest {

    private static final Instant START = Instant.parse("2026-09-01T00:00:00Z");
    private static final Instant END = Instant.parse("2026-09-04T00:00:00Z"); // splits into 3 windows of 1 day
    private static final String VISIT_TYPE = "FOLLOW_UP";

    @Test
    void firstWindowMatches_returnsResultWithoutWaitingForOthers() {
        FakeProviderPort port = FakeProviderPort.builder()
                .window(0, Flux.just(encounter("e1")))
                .window(1, Flux.never())
                .window(2, Flux.never())
                .build();

        StepVerifier.create(service(port, 3).search(accountId(), START, END, VISIT_TYPE))
                .assertNext(result -> assertThat(result.encounterId()).isEqualTo("e1"))
                .verifyComplete();
    }

    @Test
    void laterWindowRespondingFirst_winsTheRace() {
        FakeProviderPort port = FakeProviderPort.builder()
                .window(0, Flux.just(encounter("slow")).delayElements(Duration.ofMillis(300)))
                .window(1, Flux.just(encounter("fast")).delayElements(Duration.ofMillis(20)))
                .window(2, Flux.never())
                .build();

        StepVerifier.create(service(port, 3).search(accountId(), START, END, VISIT_TYPE))
                .assertNext(result -> assertThat(result.encounterId()).isEqualTo("fast"))
                .expectComplete()
                .verify(Duration.ofSeconds(2));
    }

    @Test
    void noWindowMatches_completesEmpty() {
        FakeProviderPort port = FakeProviderPort.builder()
                .window(0, Flux.empty())
                .window(1, Flux.empty())
                .window(2, Flux.empty())
                .build();

        StepVerifier.create(service(port, 3).search(accountId(), START, END, VISIT_TYPE))
                .verifyComplete();
    }

    @Test
    void cancellationOnFirstMatch_cancelsStillInFlightWindows() {
        AtomicInteger window0Cancelled = new AtomicInteger();
        AtomicInteger window2Cancelled = new AtomicInteger();

        FakeProviderPort port = FakeProviderPort.builder()
                .window(0, Flux.<Encounter>never().doOnCancel(window0Cancelled::incrementAndGet))
                // Small delay ensures all 3 windows are actively subscribed before this one
                // completes, so cancellation of the other two is unambiguous (not a same-tick
                // subscription-loop artifact).
                .window(1, Flux.just(encounter("fast")).delayElements(Duration.ofMillis(20)))
                .window(2, Flux.<Encounter>never().doOnCancel(window2Cancelled::incrementAndGet))
                .build();

        StepVerifier.create(service(port, 3).search(accountId(), START, END, VISIT_TYPE))
                .assertNext(result -> assertThat(result.encounterId()).isEqualTo("fast"))
                .expectComplete()
                .verify(Duration.ofSeconds(2));

        // Cancellation of the sibling windows propagates asynchronously (a different scheduler
        // thread than the one that delivered the winning result), so it may not have happened
        // yet the instant verify() returns -- poll instead of asserting immediately.
        await().atMost(Duration.ofSeconds(1)).untilAsserted(() -> {
            assertThat(window0Cancelled.get()).isEqualTo(1);
            assertThat(window2Cancelled.get()).isEqualTo(1);
        });
    }

    @Test
    void usesPerAccountMaxConcurrency_limitsPeakInFlightWindows() {
        Instant fiveWindowsEnd = START.plus(Duration.ofDays(5));
        AtomicInteger inFlight = new AtomicInteger();
        AtomicInteger peakInFlight = new AtomicInteger();

        FakeProviderPort port = FakeProviderPort.builder()
                // Flux.defer's supplier runs exactly at subscription time, i.e. exactly when
                // flatMap activates this window's slot; doOnComplete fires as the completion
                // signal passes through (before flatMap reacts to it) -- unlike doFinally,
                // which Reactor documents as running only after the signal has already been
                // forwarded downstream, which would make it fire "too late" to measure
                // peak concurrency accurately here.
                .allWindows(index -> Flux.defer(() -> {
                    peakInFlight.updateAndGet(p -> Math.max(p, inFlight.incrementAndGet()));
                    return Mono.delay(Duration.ofMillis(50)).then(Mono.<Encounter>empty());
                }).doOnComplete(inFlight::decrementAndGet))
                .build();

        AccountConcurrencyPolicy twoAtATime = accountId -> 2;
        SearchEncountersService service = new SearchEncountersService(
                new FixedSizeTimeWindowSplitter(), port, twoAtATime, Duration.ofDays(1));

        StepVerifier.create(service.search(accountId(), START, fiveWindowsEnd, VISIT_TYPE))
                .verifyComplete();

        assertThat(peakInFlight.get()).isLessThanOrEqualTo(2);
    }

    private static SearchEncountersService service(ProviderEncounterSearchPort port, int maxConcurrency) {
        AccountConcurrencyPolicy policy = accountId -> maxConcurrency;
        return new SearchEncountersService(new FixedSizeTimeWindowSplitter(), port, policy, Duration.ofDays(1));
    }

    private static AccountId accountId() {
        return new AccountId("account-123");
    }

    private static Encounter encounter(String id) {
        return new Encounter(id, VISIT_TYPE, Instant.parse("2026-09-01T10:00:00Z"), 1800);
    }

    /** Fake port keyed by 0-based window index, in the order {@link #search} is invoked. */
    private static final class FakeProviderPort implements ProviderEncounterSearchPort {
        private final Map<Integer, Flux<Encounter>> byIndex;
        private final BiFunction<Integer, TimeWindow, Flux<Encounter>> catchAll;
        private final AtomicInteger counter = new AtomicInteger();

        private FakeProviderPort(Map<Integer, Flux<Encounter>> byIndex, BiFunction<Integer, TimeWindow, Flux<Encounter>> catchAll) {
            this.byIndex = byIndex;
            this.catchAll = catchAll;
        }

        @Override
        public Flux<Encounter> search(AccountId accountId, TimeWindow window, String visitType) {
            int index = counter.getAndIncrement();
            return byIndex.containsKey(index) ? byIndex.get(index) : catchAll.apply(index, window);
        }

        static Builder builder() {
            return new Builder();
        }

        static final class Builder {
            private final Map<Integer, Flux<Encounter>> byIndex = new HashMap<>();
            private BiFunction<Integer, TimeWindow, Flux<Encounter>> catchAll = (index, window) -> Flux.empty();

            Builder window(int index, Flux<Encounter> flux) {
                byIndex.put(index, flux);
                return this;
            }

            Builder allWindows(Function<Integer, Flux<Encounter>> supplier) {
                this.catchAll = (index, window) -> supplier.apply(index);
                return this;
            }

            FakeProviderPort build() {
                return new FakeProviderPort(byIndex, catchAll);
            }
        }
    }
}
