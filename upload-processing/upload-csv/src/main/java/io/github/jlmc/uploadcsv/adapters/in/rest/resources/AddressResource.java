package io.github.jlmc.uploadcsv.adapters.in.rest.resources;

public record AddressResource(String address,
                              String city,
                              String regionName,
                              String countryName,
                              String zipCode,
                              CoordinatesResource coordinates
) {
}
