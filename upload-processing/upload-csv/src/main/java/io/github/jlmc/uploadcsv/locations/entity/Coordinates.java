package io.github.jlmc.uploadcsv.locations.entity;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coordinates {
    //@CsvNumber(value = "#0.00")
    @CsvBindByName
    private Double latitude;
    @CsvBindByName
    //@CsvNumber(value = "#0.00")
    private Double longitude;
}
