package io.github.jlmc.uploadcsv.locations.boundary.csv;

public record Violation(
        Long lineNumber,
        String message,
        Exception cause
) {
}
