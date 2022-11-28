package io.costax.demo.domain.exceptions;

public class UserWithSameEmailAlreadyExistsException extends BusinessException {

    private final String email;

    private UserWithSameEmailAlreadyExistsException(final String email) {
        super(String.format("Email '%s' is already in use", email));
        this.email = email;
    }

    public static UserWithSameEmailAlreadyExistsException of(final String email) {
        return new UserWithSameEmailAlreadyExistsException(email);
    }
}