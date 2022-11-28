package io.costax.springemaildemos.infratructure;

public class EmailSenderException extends RuntimeException {

    public EmailSenderException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
