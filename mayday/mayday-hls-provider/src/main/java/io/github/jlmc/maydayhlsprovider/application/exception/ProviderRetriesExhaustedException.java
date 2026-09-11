package io.github.jlmc.maydayhlsprovider.application.exception;

/**
 * Raised when the bounded retry budget (429/5xx) is exhausted without success.
 * Never retried infinitely.
 */
public final class ProviderRetriesExhaustedException extends RuntimeException {

    public ProviderRetriesExhaustedException(Throwable lastFailure) {
        super("Provider retries exhausted", lastFailure);
    }
}
