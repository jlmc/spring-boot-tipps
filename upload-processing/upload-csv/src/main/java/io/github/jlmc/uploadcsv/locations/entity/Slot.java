package io.github.jlmc.uploadcsv.locations.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class Slot {
    private LocalTime openAt;
    private LocalTime closeAt;
}
