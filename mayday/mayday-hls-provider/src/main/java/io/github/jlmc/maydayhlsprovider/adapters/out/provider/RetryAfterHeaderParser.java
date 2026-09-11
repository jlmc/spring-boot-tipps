package io.github.jlmc.maydayhlsprovider.adapters.out.provider;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Parses the {@code Retry-After} header per RFC 9110: either a non-negative integer number
 * of delay-seconds, or an HTTP-date. Returns empty when the header is absent or unparseable.
 */
final class RetryAfterHeaderParser {

    private RetryAfterHeaderParser() {
    }

    static Optional<Duration> parse(String headerValue) {
        if (headerValue == null || headerValue.isBlank()) {
            return Optional.empty();
        }
        String trimmed = headerValue.trim();
        try {
            long seconds = Long.parseLong(trimmed);
            return seconds >= 0 ? Optional.of(Duration.ofSeconds(seconds)) : Optional.empty();
        } catch (NumberFormatException _) {
            try {
                ZonedDateTime target = ZonedDateTime.parse(trimmed, DateTimeFormatter.RFC_1123_DATE_TIME);
                Duration delay = Duration.between(ZonedDateTime.now(target.getZone()), target);
                return delay.isNegative() ? Optional.of(Duration.ZERO) : Optional.of(delay);
            } catch (Exception notADate) {
                return Optional.empty();
            }
        }
    }
}
