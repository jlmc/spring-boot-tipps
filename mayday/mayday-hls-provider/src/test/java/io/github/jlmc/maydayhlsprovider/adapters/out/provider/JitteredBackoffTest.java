package io.github.jlmc.maydayhlsprovider.adapters.out.provider;

import io.github.jlmc.maydayhlsprovider.configuration.properties.ProviderProperties.RetryProperties.Backoff;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Duration;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class JitteredBackoffTest {

    private static final Duration MIN = Duration.ofMillis(100);
    private static final Duration MAX = Duration.ofSeconds(1);

    private static Backoff backoff(double jitterFactor) {
        return new Backoff(MIN, MAX, jitterFactor);
    }

    @ParameterizedTest(name = "attempt {0} without jitter -> {1} ms")
    @CsvSource({
            "1, 100",   // min * 2^0
            "2, 200",   // min * 2^1
            "3, 400",   // min * 2^2
            "4, 800",   // min * 2^3
            "5, 1000",  // 1600 capped to max
            "20, 1000"  // far beyond the cap
    })
    void withoutJitterBackoffIsExponentialAndCappedAtMax(long attemptsMade, long expectedMillis) {
        Duration delay = WebClientProviderEncounterSearchAdapter.jitteredBackoff(attemptsMade, backoff(0.0));

        assertThat(delay).isEqualTo(Duration.ofMillis(expectedMillis));
    }

    @Test
    void jitterStaysWithinTheBandAroundTheBaseDelay() {
        // attempt 2 -> base 200ms, factor 0.5 -> band [100, 300]
        Set<Long> seen = IntStream.range(0, 500)
                .mapToObj(_ -> WebClientProviderEncounterSearchAdapter.jitteredBackoff(2, backoff(0.5)))
                .peek(d -> assertThat(d).isBetween(Duration.ofMillis(100), Duration.ofMillis(300)))
                .map(Duration::toMillis)
                .collect(Collectors.toSet());

        // it really is random: not a constant, and it uses both sides of the base value
        assertThat(seen).hasSizeGreaterThan(10);
        assertThat(seen).anyMatch(ms -> ms < 200);
        assertThat(seen).anyMatch(ms -> ms > 200);
    }

    @Test
    void jitterIsAppliedAroundTheCappedValueNotTheRawExponential() {
        // attempt 10 -> raw 51200ms, capped to 1000ms; band with 0.2 is [800, 1200]
        IntStream.range(0, 300).forEach(_ -> assertThat(
                WebClientProviderEncounterSearchAdapter.jitteredBackoff(10, backoff(0.2)))
                .isBetween(Duration.ofMillis(800), Duration.ofMillis(1200)));
    }

    @Test
    void fullJitterNeverGoesNegative() {
        // factor 1.0 -> low bound would be capped - capped = 0; must never fall below zero
        IntStream.range(0, 500).forEach(_ -> assertThat(
                WebClientProviderEncounterSearchAdapter.jitteredBackoff(1, backoff(1.0)))
                .isBetween(Duration.ZERO, Duration.ofMillis(200)));
    }

    @Test
    void zeroJitterIsDeterministic() {
        Set<Duration> distinct = IntStream.range(0, 50)
                .mapToObj(_ -> WebClientProviderEncounterSearchAdapter.jitteredBackoff(3, backoff(0.0)))
                .collect(Collectors.toSet());

        assertThat(distinct).containsExactly(Duration.ofMillis(400));
    }
}
