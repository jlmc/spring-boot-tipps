package io.github.jlmc.uploadcsv.locations.boundary.csv.locations.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

class LocationCsvResourceTest {

    @Test
    void onEquals() {
        var locationCsvResource1 =
                LocationCsvResource.builder()
                                   .id("1")
                                   .name("loc 1")
                                   .imageUrl("https://static.nike.com/a/images/f_auto/c5fbda6e-5cb6-4fae-8a05-e47212074dbf/image.jpeg")
                                   .phoneNumber("+351123456789")
                                   .timeZone(ZoneId.of("Europe/Lisbon"))
                                   .build();

        var locationCsvResource2 = locationCsvResource1.toBuilder().build();

        Assertions.assertNotSame(locationCsvResource1, locationCsvResource2);
        Assertions.assertEquals(locationCsvResource1, locationCsvResource2);
        Assertions.assertEquals(locationCsvResource1.hashCode(), locationCsvResource2.hashCode());
    }

    @Test
    void toEntity() {

        LocationCsvResource resource =
                LocationCsvResource.builder()
                                   .id("1")
                                   .name("loc 1")
                                   .imageUrl("https://static.nike.com/a/images/f_auto/c5fbda6e-5cb6-4fae-8a05-e47212074dbf/image.jpeg")
                                   .phoneNumber("+351123456789")
                                   .address(AddressCsvResource.builder()
                                                              .address("Av1")
                                                              .city("Alverca")
                                                              .regionName("Lisabon")
                                                              .countryName("Portugal")
                                                              .build())
                                   .timeZone(ZoneId.of("Europe/Lisbon"))
                                   .businessSlotsMonday(List.of(new SlotCsvResource(LocalTime.parse("09:01"), LocalTime.parse("15:00"))))
                                   .businessSlotsTuesday(List.of(new SlotCsvResource(LocalTime.parse("09:01"), LocalTime.parse("15:00"))))
                                   .businessSlotsWednesday(List.of(new SlotCsvResource(LocalTime.parse("09:01"), LocalTime.parse("15:00"))))
                                   .businessSlotsThursday(List.of(new SlotCsvResource(LocalTime.parse("09:01"), LocalTime.parse("15:00"))))
                                   .businessSlotsFriday(List.of(new SlotCsvResource(LocalTime.parse("09:01"), LocalTime.parse("15:00"))))
                                   .businessSlotsSaturday(List.of(new SlotCsvResource(LocalTime.parse("09:01"), LocalTime.parse("15:00"))))
                                   .businessSlotsSunday(List.of(new SlotCsvResource(LocalTime.parse("09:01"), LocalTime.parse("15:00"))))
                                   .build();
        Assertions.assertNotNull(resource);
    }
}
