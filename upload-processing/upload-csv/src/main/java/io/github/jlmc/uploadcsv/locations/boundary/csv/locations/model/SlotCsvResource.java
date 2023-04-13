package io.github.jlmc.uploadcsv.locations.boundary.csv.locations.model;

import io.github.jlmc.uploadcsv.locations.entity.Slot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class SlotCsvResource {
    private LocalTime openAt;
    private LocalTime closeAt;

    public static SlotCsvResource from(Slot slot) {
        return new SlotCsvResource(slot.getOpenAt(), slot.getCloseAt());
    }

    public Slot toEntity() {
        return Slot.of(openAt, closeAt);
    }
}
