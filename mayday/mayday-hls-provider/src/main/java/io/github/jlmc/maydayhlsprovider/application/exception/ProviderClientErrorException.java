package io.github.jlmc.maydayhlsprovider.application.exception;

/**
 * The provider returned a 4xx other than 429. Not retried.
 */
public final class ProviderClientErrorException extends RuntimeException {

    private final int status;

    public ProviderClientErrorException(int status) {
        super("Provider returned client error status " + status);
        this.status = status;
    }

    public int status() {
        return status;
    }
}
