package io.github.jlmc.poc.api.exceptions.outputs;

public record ProblemDetailError(String name, String detail) {

    public ProblemDetailError {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
    }
}
