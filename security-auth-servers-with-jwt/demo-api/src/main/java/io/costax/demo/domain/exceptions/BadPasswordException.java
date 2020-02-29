package io.costax.demo.domain.exceptions;

public class BadPasswordException extends BusinessException {

    public BadPasswordException(final String message) {
        super(message);
    }
}
