package io.costax.food4u.domain.exceptions;

import io.costax.food4u.domain.model.Cooker;

import java.io.Serializable;

public class CookerNotFoundException extends ResourceNotFoundException {

    private CookerNotFoundException(final Serializable identifier) {
        super(Cooker.class, identifier);
    }

    public static CookerNotFoundException of(final Serializable identifier) {
        return new CookerNotFoundException(identifier);
    }
}
