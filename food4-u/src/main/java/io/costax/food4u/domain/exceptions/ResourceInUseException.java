package io.costax.food4u.domain.exceptions;

import java.io.Serializable;

public class ResourceInUseException extends BusinessException {
    private final Class<?> resourceClass;
    private final Serializable identifier;

    private ResourceInUseException(final Class<?> resourceClass, final Serializable identifier, String message) {
        super(message);
        this.resourceClass = resourceClass;
        this.identifier = identifier;
    }

    public static ResourceInUseException of(final Class<?> resourceClass, final Serializable identifier) {
        return new ResourceInUseException(resourceClass, identifier,
                String.format("Resource [%s] with the identifier [%s] In use", resourceClass.getSimpleName(), identifier));
    }

    public Class<?> getResourceClass() {
        return resourceClass;
    }

    public Serializable getIdentifier() {
        return identifier;
    }
}
