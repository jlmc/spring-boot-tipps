package io.github.jlmc.uploadcsv.locations.boundary;

import io.github.jlmc.uploadcsv.locations.entity.Address;
import io.github.jlmc.uploadcsv.locations.entity.Coordinates;
import io.github.jlmc.uploadcsv.locations.entity.Location;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.List;

@Component
public class LocationPopulator {

    List<Location> locations() {
        Location location1 =
                Location.builder()
                        .id("1")
                        .name("forum")
                        .imageUrl("image-1.png")
                        .phoneNumber("123")
                        .timeZone(ZoneId.of("Europe/Lisbon"))
                        .address(Address.builder()
                                        .address("Rua do Alto, nº 5 ")
                                        .city("Santa clara")
                                        .regionName("Coimbra")
                                        .zipCode("3100")
                                        .coordinates(Coordinates.builder()
                                                                .latitude(12.0)
                                                                .longitude(99.0D)
                                                                .build())
                                        .countryName("Portugal")
                                        .build())
                        .build();
        Location location2 =
                Location.builder()
                        .id("2")
                        .name("columbo")
                        .imageUrl("image-2.png")
                        .phoneNumber("125")
                        .timeZone(ZoneId.of("Europe/Lisbon"))
                        .address(Address.builder()
                                        .address("Oriente")
                                        .city("Lisboa")
                                        .regionName("Lisboa")
                                        .zipCode("3000")
                                        .countryName("Portugal")
                                        .coordinates(Coordinates.builder()
                                                                .latitude(17.093273892731)
                                                                .longitude(98.12345678D)
                                                                .build())
                                        .build())
                        .build();

        return List.of(location1, location2);
    }
}
