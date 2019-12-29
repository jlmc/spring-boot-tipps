package io.costax.food4u.domain.exceptions;

import java.io.Serializable;

public class ResourceNotFoundException extends BusinessException {

    private final Class<?> resourceClass;
    private final Serializable identifier;

    public ResourceNotFoundException(final Class<?> resourceClass, final Serializable identifier) {
       this(resourceClass, identifier, String.format("Resource [%s] with the identifier [%s] no found", resourceClass.getSimpleName(), identifier));
    }

    private ResourceNotFoundException(final Class<?> resourceClass, final Serializable identifier, String message) {
        super(message);
        this.resourceClass = resourceClass;
        this.identifier = identifier;
    }

    public Serializable getIdentifier() {
        return identifier;
    }
}
