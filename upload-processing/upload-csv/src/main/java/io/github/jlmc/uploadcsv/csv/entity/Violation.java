package io.github.jlmc.uploadcsv.csv.entity;

import java.io.Serializable;

public record Violation(
        Long lineNumber,
        String message,
        Exception cause
) implements Serializable {
}
