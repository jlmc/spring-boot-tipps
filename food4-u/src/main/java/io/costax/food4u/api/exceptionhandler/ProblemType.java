package io.costax.food4u.api.exceptionhandler;

enum ProblemType {
    ENTITY_NOT_FOUND("/entity-not-found", "Entity not found"),
    ILLEGAL_ARGUMENT("/illegal-argument", "Illegal Argument"),
    RESOURCE_IN_USE("/resource-in-use", "Resource in use"),
    BUSINESS_ERROR("/generic-business-error", "Business rule violation");

    private String title;
    private String uri;

    ProblemType(String path, String title) {
        // rename the base uri...
        this.uri = "https://dummy.io" + path;
        this.title = title;
    }

    String getTitle() {
        return title;
    }

    String getUri() {
        return uri;
    }
}
