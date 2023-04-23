package io.github.jlmc.uploadcsv.locations.boundary.resources;

public record AddressResource(String address,
                              String city,
                              String regionName,
                              String countryName,
                              String zipCode,
                              CoordinatesResource coordinates
) {
}
