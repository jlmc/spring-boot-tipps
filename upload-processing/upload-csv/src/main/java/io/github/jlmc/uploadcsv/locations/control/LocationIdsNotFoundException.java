package io.github.jlmc.uploadcsv.locations.control;

import java.util.Collection;
import java.util.stream.Collectors;

public class LocationIdsNotFoundException extends RuntimeException {
    private final Collection<String> invalidIds;

    public LocationIdsNotFoundException(Collection<String> invalidIds) {
        super("Location ids not found <[%s]>.".formatted(String.join(", ", invalidIds)));
        this.invalidIds = invalidIds;
    }
}
