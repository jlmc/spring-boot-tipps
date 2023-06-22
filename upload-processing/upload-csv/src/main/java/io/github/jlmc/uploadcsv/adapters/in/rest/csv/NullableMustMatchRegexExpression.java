package io.github.jlmc.uploadcsv.adapters.in.rest.csv;

import com.opencsv.bean.validators.MustMatchRegexExpression;

public class NullableMustMatchRegexExpression extends MustMatchRegexExpression {
    @Override
    public boolean isValid(String value) {
        return value == null || super.isValid(value);
    }
}
