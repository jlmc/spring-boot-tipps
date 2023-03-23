package io.github.jlmc.uploadcsv.locations.control;

import java.util.List;

public record CsvReadResult<T>(List<T> items, List<CsvLineViolation> violations) {

    public boolean isValid() {
        return violations == null || violations.isEmpty();
    }
}
