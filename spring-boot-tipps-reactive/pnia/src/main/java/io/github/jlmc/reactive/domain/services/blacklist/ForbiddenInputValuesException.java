package io.github.jlmc.reactive.domain.services.blacklist;

import java.util.Collection;

public class ForbiddenInputValuesException extends RuntimeException {

    private final Collection<String> invalidValues;

    public ForbiddenInputValuesException(Collection<String> invalidValues) {
        this.invalidValues = invalidValues;
    }

    public Collection<String> getInvalidValues() {
        return invalidValues;
    }
}
