package io.github.jlmc.uploadcsv.locations.boundary.resources;

import java.util.Set;

public record LocationResource(
        String id,
        String name,
        String imageUrl,
        String phoneNumber,
        String timeZone,
        AddressResource address,
        Set<DayOfWeekSlots> openHours
) {
}
