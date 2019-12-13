package io.costax.food4u.api;

public class ResourceNotFoundException extends RuntimeException {

    private final Class<?> resourceClass;

    public ResourceNotFoundException(final Class<?> resourceClass) {
        this.resourceClass = resourceClass;
    }

    public Class<?> getResourceClass() {
        return resourceClass;
    }
}
