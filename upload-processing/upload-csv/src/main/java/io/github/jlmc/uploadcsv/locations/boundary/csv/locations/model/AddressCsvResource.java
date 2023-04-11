package io.github.jlmc.uploadcsv.locations.boundary.csv.locations.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvRecurse;
import io.github.jlmc.uploadcsv.locations.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

import static io.github.jlmc.uploadcsv.locations.boundary.csv.locations.model.Columns.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class AddressCsvResource {
    @CsvBindByName(column = ADDRESS_STREET, required = true)
    String address;
    @CsvBindByName(column = ADDRESS_ZIP_CODE, required = true)
    String zipCode;
    @CsvBindByName(column = ADDRESS_CITY)
    String city;
    @CsvBindByName(column = ADDRESS_REGION)
    String regionName;
    @CsvBindByName(column = COUNTRY_NAME, required = true)
    String countryName;
    @CsvRecurse
    CoordinatesCsvResource coordinates;

    public Address toEntity() {
        return Address.builder()
                      .address(address).zipCode(zipCode).city(city).regionName(regionName).countryName(countryName)
                      .coordinates(Optional.ofNullable(coordinates).map(CoordinatesCsvResource::toEntity).orElse(null))
                      .build();

    }
}
