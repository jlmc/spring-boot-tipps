package io.costax.food4u.domain.exceptions;

public class WrongCurrentPasswordException extends BusinessException {

    private WrongCurrentPasswordException(final String email) {
        super(String.format("Current password entered does not match user '%s' password.", email));
    }

    public static WrongCurrentPasswordException of(final String email) {
        return new WrongCurrentPasswordException(email);
    }
}
