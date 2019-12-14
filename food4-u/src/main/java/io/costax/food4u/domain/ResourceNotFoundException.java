package io.costax.food4u.domain;

import java.io.Serializable;

public class ResourceNotFoundException extends RuntimeException {

    private final Class<?> resourceClass;
    private final Serializable identifier;

    private ResourceNotFoundException(final Class<?> resourceClass, final Serializable identifier) {
       this(resourceClass, identifier, String.format("Resource [%s] with the identifier [%s] no found", resourceClass.getSimpleName(), identifier));
    }

    private ResourceNotFoundException(final Class<?> resourceClass, final Serializable identifier, String message) {
        super(message);
        this.resourceClass = resourceClass;
        this.identifier = identifier;
    }

    public static ResourceNotFoundException of(final Class<?> resourceClass, final Serializable identifier) {
        return new ResourceNotFoundException(resourceClass, identifier);
    }

    public Class<?> getResourceClass() {
        return resourceClass;
    }

    public Serializable getIdentifier() {
        return identifier;
    }
}
