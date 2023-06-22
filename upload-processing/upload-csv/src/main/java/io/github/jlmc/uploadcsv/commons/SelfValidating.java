package io.github.jlmc.uploadcsv.commons;

import jakarta.validation.*;

import java.util.Set;

public class SelfValidating<T> {

    private final Validator validator;

    public SelfValidating() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    protected void validateSelf() {
        Set<ConstraintViolation<T>> violations = validator.validate((T) this);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
