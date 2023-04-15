package io.github.jlmc.uploadcsv.locations.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Coordinates {
    private Double latitude;
    private Double longitude;
}
