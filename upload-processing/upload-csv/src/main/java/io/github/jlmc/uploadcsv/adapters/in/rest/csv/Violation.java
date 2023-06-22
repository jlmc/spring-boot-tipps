package io.github.jlmc.uploadcsv.adapters.in.rest.csv;

import java.io.Serializable;

public record Violation(
        Long lineNumber,
        String message,
        Exception cause
) implements Serializable {
}
