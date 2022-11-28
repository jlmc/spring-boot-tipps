package io.costax.demo.domain.exceptions;

import java.io.Serializable;

public class BadResourceIdException extends BusinessException {

    private final Class<?> resourceClass;
    private final Serializable identifier;

    private BadResourceIdException(final Class<?> resourceClass, final Serializable identifier, String message) {
        super(message);
        this.resourceClass = resourceClass;
        this.identifier = identifier;
    }

    private BadResourceIdException(final Class<?> resourceClass, final Serializable identifier) {
        this(resourceClass, identifier, String.format("The System do not contain any %s with the identifier '%s'", resourceClass.getSimpleName(), identifier));
    }

    public static BadResourceIdException of(final Class<?> resourceClass, final Serializable identifier) {
        return new BadResourceIdException(resourceClass, identifier);
    }

    public static BadResourceIdException of(final Class<?> resourceClass, final Serializable identifier, final String message) {
        return new BadResourceIdException(resourceClass, identifier, message);
    }

    public Class<?> getResourceClass() {
        return resourceClass;
    }

    public Serializable getIdentifier() {
        return identifier;
    }
}
