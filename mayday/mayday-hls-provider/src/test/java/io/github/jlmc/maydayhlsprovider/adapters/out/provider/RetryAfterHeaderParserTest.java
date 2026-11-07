package io.github.jlmc.maydayhlsprovider.adapters.out.provider;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class RetryAfterHeaderParserTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    void absentOrBlankHeaderIsEmpty(String header) {
        assertThat(RetryAfterHeaderParser.parse(header)).isEmpty();
    }

    @Test
    void delaySecondsAreParsed() {
        assertThat(RetryAfterHeaderParser.parse("120")).contains(Duration.ofSeconds(120));
    }

    @Test
    void zeroSecondsIsAValidImmediateRetry() {
        // boundary: 0 is non-negative, so it must be accepted (not treated as invalid)
        assertThat(RetryAfterHeaderParser.parse("0")).contains(Duration.ZERO);
    }

    @Test
    void surroundingWhitespaceIsTrimmed() {
        assertThat(RetryAfterHeaderParser.parse("  30 ")).contains(Duration.ofSeconds(30));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "-120"})
    void negativeSecondsAreRejected(String header) {
        assertThat(RetryAfterHeaderParser.parse(header)).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "1.5", "12s", "Not, a date", "Wed, 99 Foo 2025 99:99:99 GMT"})
    void garbageIsEmpty(String header) {
        assertThat(RetryAfterHeaderParser.parse(header)).isEmpty();
    }

    @Test
    void futureHttpDateBecomesRemainingDelay() {
        String header = httpDate(ZonedDateTime.now(ZoneOffset.UTC).plusSeconds(120));

        Optional<Duration> parsed = RetryAfterHeaderParser.parse(header);

        // HTTP-date has 1s precision and time passes between formatting and parsing.
        assertThat(parsed).isPresent();
        assertThat(parsed.get()).isBetween(Duration.ofSeconds(100), Duration.ofSeconds(120));
    }

    @Test
    void pastHttpDateIsClampedToZero() {
        String header = httpDate(ZonedDateTime.now(ZoneOffset.UTC).minusSeconds(120));

        assertThat(RetryAfterHeaderParser.parse(header)).contains(Duration.ZERO);
    }

    @Test
    void httpDateInAnotherZoneIsComparedCorrectly() {
        // same instant (+120s) expressed with a +02:00 offset must give the same ~120s delay
        ZonedDateTime target = ZonedDateTime.now(ZoneOffset.ofHours(2)).plusSeconds(120);
        String header = DateTimeFormatter.RFC_1123_DATE_TIME.format(target);

        Optional<Duration> parsed = RetryAfterHeaderParser.parse(header);

        assertThat(parsed).isPresent();
        assertThat(parsed.get()).isBetween(Duration.ofSeconds(100), Duration.ofSeconds(120));
    }

    private static String httpDate(ZonedDateTime date) {
        return DateTimeFormatter.RFC_1123_DATE_TIME.format(date);
    }
}
