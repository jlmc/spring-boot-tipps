package io.github.jlmc.uploadcsv.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Address {
    String regionName;
    String city;
    String zipCode;
    Coordinates coordinates;
    private String address;
    private String countryName;
}
