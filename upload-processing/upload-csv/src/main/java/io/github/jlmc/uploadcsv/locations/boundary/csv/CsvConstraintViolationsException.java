package io.github.jlmc.uploadcsv.locations.boundary.csv;

import java.util.Collection;

public class CsvConstraintViolationsException extends RuntimeException {

    private final Collection<Violation> violations;

    public CsvConstraintViolationsException(Collection<Violation> violations) {
        super("Found " + violations.size() + " violation(s) in the input data.");
        this.violations = violations;
    }

    @SuppressWarnings("unused")
    public Collection<Violation> getViolations() {
        return violations;
    }
}
