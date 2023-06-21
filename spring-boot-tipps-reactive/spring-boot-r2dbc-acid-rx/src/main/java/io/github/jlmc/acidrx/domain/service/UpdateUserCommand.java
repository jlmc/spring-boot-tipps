package io.github.jlmc.acidrx.domain.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record UpdateUserCommand(@NotBlank String userId, @NotBlank String name, @Size(max = 500) String notes) {
    public UpdateUserCommand {
        Validator validator = BeanProvider.getValidator();
        Set<ConstraintViolation<UpdateUserCommand>> violations = validator.validate(this);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

}
