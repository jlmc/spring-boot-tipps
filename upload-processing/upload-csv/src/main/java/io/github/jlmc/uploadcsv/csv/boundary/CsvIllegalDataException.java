package io.github.jlmc.uploadcsv.csv.boundary;

import java.io.Serializable;
import java.util.Optional;

public class CsvIllegalDataException extends IllegalArgumentException {

    private final Error error;

    public CsvIllegalDataException(String errorName, String errorDescription, String message, Throwable cause) {
        super(message, cause);

        if (errorName != null && errorDescription != null) {
            this.error = new Error(errorName, errorDescription);
        } else {
            this.error = null;
        }
    }

    public Optional<Error> getError() {
        return Optional.ofNullable(error);
    }

    public record Error(String name, String description) implements Serializable {}
}
