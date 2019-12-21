package io.costax.food4u.api.exceptionhandler;

enum ProblemType {
    RESOURCE_NOT_FOUND("/resource-not-found", "Resource not found"),
    ILLEGAL_ARGUMENT("/illegal-argument", "Illegal Argument"),
    RESOURCE_IN_USE("/resource-in-use", "Resource in use"),
    BUSINESS_ERROR("/generic-business-error", "Business rule violation"),
    MESSAGE_NOT_READABLE("/message-not-readable", "Incomprehensible Message"),
    URI_PARAMETER_INVALID("/uri-parameter-invalid", "URI invalid parameter");

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
