package io.github.jlmc.sbvalidation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Cross Check Validator for custom validation.
 */
@Documented
@Constraint(validatedBy = CrossCheckConstraintValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CrossCheck {

    String message() default "{validation.constraints.CrossCheck.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
