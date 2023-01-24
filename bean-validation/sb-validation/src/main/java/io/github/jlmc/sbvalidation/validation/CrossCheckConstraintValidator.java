package io.github.jlmc.sbvalidation.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * CrossCheck Constraint Validator, delegate the validation for the ValidEntity.
 */
public class CrossCheckConstraintValidator implements ConstraintValidator<CrossCheck, ValidableEntity> {

    @Override
    public void initialize(CrossCheck constraintAnnotation) {
        // intentionally empty
    }

    @Override
    public boolean isValid(ValidableEntity entity, ConstraintValidatorContext context) {
        return entity.isValid();
    }
}
