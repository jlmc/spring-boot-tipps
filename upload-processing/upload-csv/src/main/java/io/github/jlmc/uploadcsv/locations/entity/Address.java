package io.github.jlmc.uploadcsv.locations.entity;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvRecurse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    String regionName;
    String city;
    String zipCode;
    @CsvRecurse
    Coordinates coordinates;
    @CsvBindByName(column = "address_address", required = true)
    private String address;
    @CsvBindByName(column = "address_country_name")
    private String countryName;
}
