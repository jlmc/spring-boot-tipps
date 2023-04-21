package io.github.jlmc.uploadcsv.csv.boundary;

import com.opencsv.bean.validators.MustMatchRegexExpression;

public class NullableMustMatchRegexExpression extends MustMatchRegexExpression {
    @Override
    public boolean isValid(String value) {
        return value == null || super.isValid(value);
    }
}
