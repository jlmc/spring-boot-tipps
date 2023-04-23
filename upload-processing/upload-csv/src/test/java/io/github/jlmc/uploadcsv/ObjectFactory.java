package io.github.jlmc.uploadcsv;

import io.github.jlmc.uploadcsv.locations.entity.Address;
import io.github.jlmc.uploadcsv.locations.entity.Coordinates;
import io.github.jlmc.uploadcsv.locations.entity.Location;
import io.github.jlmc.uploadcsv.locations.entity.Slot;
import org.jetbrains.annotations.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class ObjectFactory {

    private ObjectFactory() {
        throw new UnsupportedOperationException();
    }

    public static Coordinates coordinates() {
        return new Coordinates(-90.0, 180.0);
    }

    public static Address address() {
        return Address.builder()
                      .address("R. de Campolide Nº 99")
                      .countryName("United States")
                      .regionName("Texas")
                      .city("Austin")
                      .zipCode("1050-123")
                      .coordinates(coordinates())
                      .build();
    }

    public static Slot slot() {
        return slot("09:00", "19:00");
    }

    public static Slot slot(String isoStartAt, String isoClosetAt) {
        return Slot.of(LocalTime.parse(isoStartAt), LocalTime.parse(isoClosetAt));
    }

    public static Location location() {
        return location(null);
    }

    public static Location location(String id) {
        return location(id, "1");
    }

    public static Location location(String id, String accountId) {
        return location(id, accountId, "United States");
    }

    public static Location location(String id, String accountId, String addressCountryName) {
        return Location.builder()
                       .id(id)
                       .accountId(accountId)
                       .name("fake lab")
                       .imageUrl("https://fake-images/1.png")
                       .phoneNumber("+351987987987")
                       .timeZone(ZoneId.of("America/Chicago"))
                       .address(address().toBuilder().countryName(addressCountryName).build())
                       .openHours(openHours())
                       .build();
    }

    @NotNull
    private static Map<DayOfWeek, Set<Slot>> openHours() {
        return Arrays.stream(DayOfWeek.values())
                     .map(dayOfWeek -> Map.entry(dayOfWeek, Set.of(slot())))
                     .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
