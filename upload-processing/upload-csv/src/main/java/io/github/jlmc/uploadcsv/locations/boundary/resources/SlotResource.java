package io.github.jlmc.uploadcsv.locations.boundary.resources;

import java.time.LocalTime;

public record SlotResource(
        LocalTime openAt,
        LocalTime closeAt
) {
}
