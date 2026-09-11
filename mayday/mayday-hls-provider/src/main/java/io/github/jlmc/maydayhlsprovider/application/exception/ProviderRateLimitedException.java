package io.github.jlmc.maydayhlsprovider.application.exception;

import java.time.Duration;

/**
 * The provider responded 429. {@code retryAfter} is the parsed {@code Retry-After} delay,
 * or {@code null} when the provider did not supply one.
 */
public final class ProviderRateLimitedException extends RuntimeException {

    private final Duration retryAfter;

    public ProviderRateLimitedException(Duration retryAfter) {
        super("Provider rate limited the request" + (retryAfter != null ? " (retryAfter=" + retryAfter + ")" : ""));
        this.retryAfter = retryAfter;
    }

    public Duration retryAfter() {
        return retryAfter;
    }
}
