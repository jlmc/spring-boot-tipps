package io.costax.food4u.api.exceptionhandler;

public enum ProblemType {
    ENTITY_NOT_FOUND("/entity-not-found", "Entity not found");

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
