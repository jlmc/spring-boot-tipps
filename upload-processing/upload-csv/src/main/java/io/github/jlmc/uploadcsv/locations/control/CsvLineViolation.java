package io.github.jlmc.uploadcsv.locations.control;

public class CsvLineViolation {

    private final long lineNumber;
    private final String message;

    public CsvLineViolation(long lineNumber, String message) {
        this.lineNumber = lineNumber;
        this.message = message;
    }

    @Override
    public String toString() {
        return "The line %d %s".formatted(lineNumber, message);
    }
}
