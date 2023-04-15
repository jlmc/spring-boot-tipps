package io.github.jlmc.uploadcsv.locations.boundary.csv;

import java.util.Optional;

public class CsvIllegalDataException extends IllegalArgumentException {

    private final Error error;

    public CsvIllegalDataException(String errorName, String errorDescription, Throwable cause) {
        super(cause.getMessage(), cause);

        if (errorName != null && errorDescription != null) {
            this.error = new Error(errorName, errorDescription);
        } else {
            this.error = null;
        }
    }

    public Optional<Error> getError() {
        return Optional.ofNullable(error);
    }

    public record Error(String name, String description) {}
}
