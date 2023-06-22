package io.github.jlmc.uploadcsv.adapters.in.rest.csv.models;

import com.opencsv.bean.CsvBindByName;
import io.github.jlmc.uploadcsv.domain.Coordinates;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static io.github.jlmc.uploadcsv.adapters.in.rest.csv.models.Columns.LATITUDE;
import static io.github.jlmc.uploadcsv.adapters.in.rest.csv.models.Columns.LONGITUDE;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoordinatesCsvResource {
    @CsvBindByName(column = LATITUDE)
    Double latitude;
    @CsvBindByName(column = LONGITUDE)
    Double longitude;

    public Coordinates toEntity() {
        return Coordinates.builder().latitude(latitude).longitude(longitude).build();
    }
}
