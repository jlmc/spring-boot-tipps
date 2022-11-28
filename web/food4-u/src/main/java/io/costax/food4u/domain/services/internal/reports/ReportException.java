package io.costax.food4u.domain.services.internal.reports;

public class ReportException extends RuntimeException {

    public ReportException(final String message, final Exception cause) {
        super(message, cause);
    }
}
