package io.costax.food4u.core.validation;

import org.springframework.beans.BeanUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ValidationException;
import java.math.BigDecimal;

public class ZeroValueIncludesDescriptionConstraintValidator implements ConstraintValidator<ZeroValueIncludesDescription, Object> {

    private String descriptionPropertyName;
    private String numericPropertyName;

    @Override
    public void initialize(final ZeroValueIncludesDescription constraintAnnotation) {
        descriptionPropertyName = constraintAnnotation.descriptionPropertyName();
        numericPropertyName = constraintAnnotation.numericPropertyName();
    }

    @Override
    public boolean isValid(final Object obj, final ConstraintValidatorContext context) {
        try {

            BigDecimal value = (BigDecimal)
                    BeanUtils
                            .getPropertyDescriptor(obj.getClass(), this.numericPropertyName)
                            .getReadMethod()
                            .invoke(obj);

            String description = (String)
                    BeanUtils
                            .getPropertyDescriptor(obj.getClass(), this.descriptionPropertyName)
                            .getReadMethod()
                            .invoke(obj);

            return (value == null || BigDecimal.ZERO.compareTo(value) != 0) || description != null;

        } catch (Exception e) {
            throw new ValidationException(e);
        }
    }
}
