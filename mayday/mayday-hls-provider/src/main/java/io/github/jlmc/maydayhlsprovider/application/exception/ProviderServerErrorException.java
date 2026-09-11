package io.github.jlmc.maydayhlsprovider.application.exception;

public final class ProviderServerErrorException extends RuntimeException {

    private final int status;

    public ProviderServerErrorException(int status) {
        super("Provider returned server error status " + status);
        this.status = status;
    }

    public int status() {
        return status;
    }
}
