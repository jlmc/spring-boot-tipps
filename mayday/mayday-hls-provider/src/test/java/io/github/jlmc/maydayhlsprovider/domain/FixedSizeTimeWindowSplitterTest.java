package io.github.jlmc.maydayhlsprovider.domain;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FixedSizeTimeWindowSplitterTest {

    private final FixedSizeTimeWindowSplitter splitter = new FixedSizeTimeWindowSplitter();

    @Test
    void intervalSmallerThanWindow_returnsSinglePartialWindow() {
        Instant start = Instant.parse("2026-09-01T00:00:00Z");
        Instant end = Instant.parse("2026-09-01T06:00:00Z");

        List<TimeWindow> windows = splitter.split(start, end, Duration.ofDays(1));

        assertThat(windows).containsExactly(new TimeWindow(start, end));
    }

    @Test
    void intervalDividesEvenly_returnsExactWindows() {
        Instant start = Instant.parse("2026-09-01T00:00:00Z");
        Instant end = Instant.parse("2026-09-04T00:00:00Z");

        List<TimeWindow> windows = splitter.split(start, end, Duration.ofDays(1));

        assertThat(windows).containsExactly(
                new TimeWindow(Instant.parse("2026-09-01T00:00:00Z"), Instant.parse("2026-09-02T00:00:00Z")),
                new TimeWindow(Instant.parse("2026-09-02T00:00:00Z"), Instant.parse("2026-09-03T00:00:00Z")),
                new TimeWindow(Instant.parse("2026-09-03T00:00:00Z"), Instant.parse("2026-09-04T00:00:00Z")));
    }

    @Test
    void intervalHasPartialLastWindow_lastWindowIsClampedToEnd() {
        Instant start = Instant.parse("2026-09-01T00:00:00Z");
        Instant end = Instant.parse("2026-09-03T12:00:00Z");

        List<TimeWindow> windows = splitter.split(start, end, Duration.ofDays(1));

        assertThat(windows).containsExactly(
                new TimeWindow(Instant.parse("2026-09-01T00:00:00Z"), Instant.parse("2026-09-02T00:00:00Z")),
                new TimeWindow(Instant.parse("2026-09-02T00:00:00Z"), Instant.parse("2026-09-03T00:00:00Z")),
                new TimeWindow(Instant.parse("2026-09-03T00:00:00Z"), Instant.parse("2026-09-03T12:00:00Z")));
    }

    @Test
    void startEqualsEnd_returnsEmptyList() {
        Instant instant = Instant.parse("2026-09-01T00:00:00Z");

        assertThat(splitter.split(instant, instant, Duration.ofDays(1))).isEmpty();
    }

    @Test
    void startAfterEnd_returnsEmptyList() {
        Instant start = Instant.parse("2026-09-02T00:00:00Z");
        Instant end = Instant.parse("2026-09-01T00:00:00Z");

        assertThat(splitter.split(start, end, Duration.ofDays(1))).isEmpty();
    }

    @Test
    void nonPositiveWindowSize_throws() {
        Instant start = Instant.parse("2026-09-01T00:00:00Z");
        Instant end = Instant.parse("2026-09-02T00:00:00Z");

        assertThatThrownBy(() -> splitter.split(start, end, Duration.ZERO))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> splitter.split(start, end, Duration.ofSeconds(-1)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
