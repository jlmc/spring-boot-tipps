package io.github.jlmc.maydayhlsprovider.domain;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class FixedSizeTimeWindowSplitter implements TimeWindowSplitter {

    @Override
    public List<TimeWindow> split(Instant start, Instant end, Duration windowSize) {
        Objects.requireNonNull(start, "start must not be null");
        Objects.requireNonNull(end, "end must not be null");
        Objects.requireNonNull(windowSize, "windowSize must not be null");
        if (windowSize.isZero() || windowSize.isNegative()) {
            throw new IllegalArgumentException("windowSize must be positive");
        }
        if (!start.isBefore(end)) {
            return List.of();
        }

        List<TimeWindow> windows = new ArrayList<>();
        Instant cursor = start;
        while (cursor.isBefore(end)) {
            Instant next = cursor.plus(windowSize);
            Instant windowEnd = next.isAfter(end) ? end : next;
            windows.add(new TimeWindow(cursor, windowEnd));
            cursor = next;
        }
        return List.copyOf(windows);
    }
}
