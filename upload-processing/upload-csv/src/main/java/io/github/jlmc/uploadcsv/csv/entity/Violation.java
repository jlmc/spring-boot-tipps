package io.github.jlmc.uploadcsv.csv.entity;

public record Violation(
        Long lineNumber,
        String message,
        Exception cause
) {
}
