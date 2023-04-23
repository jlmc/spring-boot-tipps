package io.github.jlmc.uploadcsv.locations.boundary.csv.locations;

import io.github.jlmc.uploadcsv.csv.boundary.CsvIllegalDataException;
import io.github.jlmc.uploadcsv.csv.entity.CsvReaderResult;
import io.github.jlmc.uploadcsv.locations.entity.Coordinates;
import io.github.jlmc.uploadcsv.locations.entity.Location;
import io.github.jlmc.uploadcsv.locations.entity.Slot;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Set;

import static io.github.jlmc.uploadcsv.Resources.classPathResourceContent;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.TUESDAY;
import static org.junit.jupiter.api.Assertions.*;

class LocationsCsvReaderTest {

    public static final String ACCOUNT_ID = "account-123";
    private final LocationsCsvReader sut = new LocationsCsvReader();

    @Test
    void readInputData() {
        String content = classPathResourceContent("payloads/locations/post-locations-bulk-updates.csv");
        var result = sut.read(ACCOUNT_ID, content);


        assertNotNull(result);
        assertEquals(3, result.items().size());
        var location = result.items().get(0);
        assertNotNull(location);
        assertEquals("123", location.getId());
        assertEquals("South Lyon", location.getName());
        assertEquals("796 Hillcrest Ave", location.getAddress().getAddress());
        assertEquals("48178", location.getAddress().getZipCode());
        assertEquals("Los Angeles", location.getAddress().getCity());
        assertEquals("California", location.getAddress().getRegionName());
        assertEquals("United States", location.getAddress().getCountryName());
        assertEquals(new Coordinates(12.7, 34.5), location.getAddress().getCoordinates());
        assertEquals("https://static.nike.com/a/images/f_auto/c5fbda6e-5cb6-4fae-8a05-e47212074dbf/image.jpeg", location.getImageUrl());
        assertEquals(ZoneId.of("America/Los_Angeles"), location.getTimeZone());
        assertEquals("+12029182132", location.getPhoneNumber());
        assertEquals(2, location.getOpenHours().size());
        assertEquals(
                Set.of(
                        Slot.of(LocalTime.parse("10:00"), LocalTime.parse("13:00")),
                        Slot.of(LocalTime.parse("14:00"), LocalTime.parse("20:00"))
                ),
                location.getOpenHours().get(MONDAY)
        );
        assertEquals(
                Set.of(
                        Slot.of(LocalTime.parse("10:00"), LocalTime.parse("13:00"))
                ),
                location.getOpenHours().get(TUESDAY)
        );
    }

    @Test
    void readInputWithViolations() {
        var content =  classPathResourceContent("payloads/locations/post-locations-bulk-with-violations.csv");
        var result = sut.read(ACCOUNT_ID, content);

        assertNotNull(result);
        assertFalse(result.isValid());
        assertEquals(3, result.violations().size());
        assertEquals(2, result.violations().get(0).lineNumber());
        assertEquals(2, result.violations().get(1).lineNumber());
        assertEquals(4, result.violations().get(2).lineNumber());
        assertEquals("Field 'name' is mandatory but no value was provided.", result.violations().get(0).message());
        assertEquals("The csv slots periods must have a start and a close element, the value 02:00pm can be parsed to a Slot.", result.violations().get(1).message());
        assertEquals("Field imageUrl value \"https://example-125.com\" did not match expected format of ^(http|https)://[a-zA-Z0-9-.]+.[a-zA-Z]{2,}(/[^/]*)*/[^/]+.(jpg|jpeg|png|webp)$", result.violations().get(2).message());
    }

    @Test
    void readIllegalInputData() {
        String csvContent = classPathResourceContent("payloads/locations/post-locations-bulk-with-invalid-header.csv");

        var exception = assertThrows(
                CsvIllegalDataException.class,
                () -> sut.read(ACCOUNT_ID, csvContent)
        );

        assertEquals("Error capturing CSV header!", exception.getMessage());
        assertNotNull(exception.getCause());
        assertNotNull(exception.getError().orElse(null));
        assertEquals(new CsvIllegalDataException.Error(
                "Csv Header",
                "Header is missing required fields [LOCATION_NAME]. " +
                "The list of headers encountered is [ID,NAME,ADDRESS_STREET," +
                "ADDRESS_ZIP_CODE,ADDRESS_CITY,ADDRESS_REGION,COUNTRY_NAME," +
                "LATITUDE,LONGITUDE,IMAGE_URL,CONTACT_PHONE,TIMEZONE," +
                "BUSINESS_MON,BUSINESS_TUE,BUSINESS_WED,BUSINESS_THU," +
                "BUSINESS_FRI,BUSINESS_SAT,BUSINESS_SUN]."), exception.getError().get());
    }


    @Test
    void read5MBFiles() {
        String resource = classPathResourceContent("payloads/locations/post-5MB.csv");

        Instant stated = Instant.now();
        CsvReaderResult<Location> result = sut.read("X1", resource);
        Instant ended = Instant.now();

        Duration duration = Duration.between(stated, ended);

        assertTrue(result.isValid());


    }
}
