package io.costax.food4u.core.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

public class MultipleConstraintValidator implements ConstraintValidator<Multiple, Number> {

    private int number;

    @Override
    public void initialize(final Multiple constraintAnnotation) {
        this.number = constraintAnnotation.number();
    }

    @Override
    public boolean isValid(final Number value, final ConstraintValidatorContext context) {
        if (value == null) return true;

        var decimalValue = BigDecimal.valueOf(value.doubleValue());
        var decimalNumber = BigDecimal.valueOf(number);
        var rest = decimalValue.remainder(decimalNumber);

        return BigDecimal.ZERO.compareTo(rest) == 0;
    }
}
