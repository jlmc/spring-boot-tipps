package io.costax.food4u.core.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, PARAMETER, FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = FileContentTypeConstraintValidator.class)
public @interface FileContentType {

    String message() default "{FileContentType.message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    String[] allowed();
}
