package io.github.jlmc.uploadcsv.locations.boundary.mappers;

import io.github.jlmc.uploadcsv.ObjectFactory;
import io.github.jlmc.uploadcsv.locations.boundary.resources.AddressResource;
import io.github.jlmc.uploadcsv.locations.boundary.resources.CoordinatesResource;
import io.github.jlmc.uploadcsv.locations.boundary.resources.LocationResource;
import io.github.jlmc.uploadcsv.locations.entity.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LocationResourceMapperTest {

    @Test
    void locationEntityToLocationResource() {
        Location location = ObjectFactory.location("1", "account-X");

        LocationResource resource = LocationResourceMapper.MAPPER.toResource(location);

        assertNotNull(resource);
        assertEquals(location.getName(), resource.name());
        assertEquals(location.getImageUrl(), resource.imageUrl());
        assertEquals(location.getPhoneNumber(), resource.phoneNumber());
        assertEquals(location.getId(), resource.id());
        assertEquals(location.getTimeZone().getId(), resource.timeZone());
        var expectedAddress =
                new AddressResource(
                        location.getAddress().getAddress(),
                        location.getAddress().getCity(),
                        location.getAddress().getRegionName(),
                        location.getAddress().getCountryName(),
                        location.getAddress().getZipCode(),
                        new CoordinatesResource(location.getAddress().getCoordinates().getLatitude(), location.getAddress().getCoordinates().getLongitude()));

        assertEquals(expectedAddress, resource.address());

    }
}
