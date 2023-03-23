package io.github.jlmc.uploadcsv.locations.boundary.csv;

import java.util.List;
import java.util.Optional;

public record CsvReaderResult<T>(
        List<T> items,
        List<Violation> violations
) {

    public boolean isValid() {
        return Optional.ofNullable(violations).map(List::isEmpty).orElse(true);
    }
}
