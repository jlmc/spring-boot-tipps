package io.github.jlmc.maydayhlsprovider.domain;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Splits a [start, end) interval into fixed-size {@link TimeWindow}s.
 * Spring-free: the Application Core depends only on this abstraction, never on Spring/HTTP.
 */
public interface TimeWindowSplitter {

    /**
     * @return windows covering [start, end) in order; empty when {@code start >= end}.
     */
    List<TimeWindow> split(Instant start, Instant end, Duration windowSize);
}
