package io.github.jlmc.uploadcsv.adapters.in.rest.resources;

import java.time.LocalTime;

public record SlotResource(
        LocalTime openAt,
        LocalTime closeAt
) {
}
