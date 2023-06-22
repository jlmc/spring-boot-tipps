package io.github.jlmc.uploadcsv;

import io.github.jlmc.uploadcsv.adapters.in.rest.csv.*;
import io.github.jlmc.uploadcsv.domain.Address;
import io.github.jlmc.uploadcsv.domain.Coordinates;
import io.github.jlmc.uploadcsv.domain.Location;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.io.Writer;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OpenCsvDrivingTest {

    private static final String EXPECTED_CSV = """
            LOCATION_ID,LOCATION_NAME,ADDRESS_STREET,ADDRESS_ZIP_CODE,ADDRESS_CITY,ADDRESS_REGION,COUNTRY_NAME,LATITUDE,LONGITUDE,IMAGE_URL,CONTACT_PHONE,TIMEZONE,BUSINESS_MON,BUSINESS_TUE,BUSINESS_WED,BUSINESS_THU,BUSINESS_FRI,BUSINESS_SAT,BUSINESS_SUN
            1,forum,"Rua do Alto, nº 5 ",3100,Santa clara,Coimbra,Portugal,12.0,99.0,image-1.png,123,Europe/Lisbon,,,,,,,
            """;
    final String text = """
            "LOCATION_ID","LOCATION_NAME","ADDRESS_STREET","ADDRESS_ZIP_CODE","ADDRESS_CITY","ADDRESS_REGION","COUNTRY_NAME","LATITUDE","LONGITUDE","IMAGE_URL","CONTACT_PHONE","TIMEZONE","BUSINESS_MON","BUSINESS_TUE","BUSINESS_WED","BUSINESS_THU","BUSINESS_FRI","BUSINESS_SAT","BUSINESS_SUN"
            "6434b20bb62491116a7b1acd","South Lyon","796 Hillcrest Ave","48178","Los Angeles","California","United States","12.7","34.5","https://static.nike.com/image.jpg","+12029182132","America/Los_Angeles","10:00AM-01:00PM","","","","","",""
            "6434b20bb62491116a7b1ad0","Lisboa","Forum Avenue","1234","Almada","Lisbon","Portugal","","","https://static.nike.com/image.jpg","+12029182134","Europe/Lisbon","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM"
            "6434b20bb62491116a7b1ad1","Porto","Forum Avenue","1234","Almada","Lisbon","Portugal","","","https://static.nike.com/image.jpg","+12029182134","Europe/Lisbon","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM"
            "6434b20bb62491116a7b1ad2","Amadora","Forum Avenue","1234","Almada","Lisbon","Portugal","","","https://static.nike.com/image.jpg","+12029182134","Europe/Lisbon","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM"
            "6434b20bb62491116a7b1ad3","Pombal","Forum Avenue","1234","Almada","Lisbon","Portugal","","","https://static.nike.com/image.jpg","+12029182134","Europe/Lisbon","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM"
            "6434b20bb62491116a7b1ad4","Leiria","Forum Avenue","1234","Almada","Lisbon","Portugal","","","https://static.nike.com/image.jpg","+12029182134","Europe/Lisbon","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM"
            "6434b20bb62491116a7b1ad5","Montemor","Forum Avenue","1234","Almada","Lisbon","Portugal","","","https://static.nike.com/image.jpg","+12029182134","Europe/Lisbon","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM"
            "6434b20bb62491116a7b1ad6","Figuera","Forum Avenue","1234","Almada","Lisbon","Portugal","","","https://static.nike.com/image.jpg","+12029182134","Europe/Lisbon","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM"
            "6434b20bb62491116a7b1acf","Coimbra","Forum Avenue","1234","Almada","Lisbon","Portugal","","","https://static.nike.com/image.jpg","+12029182134","Europe/Lisbon","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM"
            "6434b20bb62491116a7b1ace","Almada","Forum Avenue","1234","Almada","Lisbon","Portugal","","","https://static.nike.com/image.jpg","+12029182134","Europe/Lisbon","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM","10:00AM-01:00PM|02:00PM-08:00PM"
            """;
    final CsvReader<Location> csvReader = new LocationsCsvReader();
    final CsvWriter<Location> csvWriter = new LocationsCsvWriter();

    List<Location> locations = new ArrayList<>();

    @BeforeAll
    public static void beforeAll() {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
    }

    private static void assertViolation(Long expectedLine, String expectedMessage, Violation givenViolation) {
        assertEquals(expectedLine, givenViolation.lineNumber());
        assertEquals(expectedMessage, givenViolation.message());
    }

    @BeforeEach
    void setUp() {
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

        locations = List.of(location1);
    }

    @Test
    void beansToCSV() {
        Writer writer = csvWriter.write(locations, new StringWriter());

        System.out.println(writer);

        assertEquals(EXPECTED_CSV, writer.toString());
    }

    @Test
    void csvToBeans() {
        var result = csvReader.read("1234", text);

        System.out.println(result.items());

        assertEquals(10, result.items().size());
    }

    @Test
    void readValidation() {
        String inputWithErrors =
                """
                        "LOCATION_ID","LOCATION_NAME","ADDRESS_STREET","ADDRESS_ZIP_CODE","ADDRESS_CITY","ADDRESS_REGION","COUNTRY_NAME","LATITUDE","LONGITUDE","IMAGE_URL","CONTACT_PHONE","TIMEZONE","BUSINESS_MON","BUSINESS_TUE","BUSINESS_WED","BUSINESS_THU","BUSINESS_FRI","BUSINESS_SAT","BUSINESS_SUN"
                        "6434b20bb62491116a7b1acd","","796 Hillcrest Ave","48178","Los Angeles","California","United States","12.7","34.5","https://example-123.com","+12029182132","America/not-valid","10:00am","","","","","",""
                        """;

        CsvReaderResult<Location> result = csvReader.read("1234", inputWithErrors);

        assertNotNull(result);
        assertNotNull(result.items());
        assertFalse(result.isValid());
        assertEquals(4, result.violations().size());

        assertViolation(2L, "Field 'name' is mandatory but no value was provided.", result.violations().get(0));
        assertViolation(2L, "Field imageUrl value \"https://example-123.com\" did not match expected format of ^(http|https)://[a-zA-Z0-9-.]+.[a-zA-Z]{2,}(/[^/]*)*/[^/]+.(jpg|jpeg|png|webp)$", result.violations().get(1));
        assertViolation(2L, "'America/not-valid' is not in the zones list defined by IANA.", result.violations().get(2));
        assertViolation(2L, "The csv slots periods must have a start and a close element, the value 10:00am can be parsed to a Slot.", result.violations().get(3));

    }

    @Test
    void customMapper() {
        String csv = """
                "LOCATION_ID","LOCATION_NAME","ADDRESS_STREET","ADDRESS_ZIP_CODE","ADDRESS_CITY","ADDRESS_REGION","COUNTRY_NAME","LATITUDE","LONGITUDE","IMAGE_URL","CONTACT_PHONE","TIMEZONE","BUSINESS_MON","BUSINESS_TUE","BUSINESS_WED","BUSINESS_THU","BUSINESS_FRI","BUSINESS_SAT","BUSINESS_SUN"
                "6434b20bb62491116a7b1acd","South Lyon","796 Hillcrest Ave","48178","Los Angeles","California","United States","12.7","34.5","https://static.nike.com/image.jpg","+12029182132","America/Los_Angeles","10:00am-01:00pm","","","","","",""
                """;

        CsvReaderResult<Location> result = csvReader.read("1234", csv);

        assertTrue(result.isValid());
        assertEquals(1, result.items().size());
        System.out.println(result);


        Writer write = csvWriter.write(result.items(), new StringWriter());

        System.out.println(write);

        String expectedCsv = """
                LOCATION_ID,LOCATION_NAME,ADDRESS_STREET,ADDRESS_ZIP_CODE,ADDRESS_CITY,ADDRESS_REGION,COUNTRY_NAME,LATITUDE,LONGITUDE,IMAGE_URL,CONTACT_PHONE,TIMEZONE,BUSINESS_MON,BUSINESS_TUE,BUSINESS_WED,BUSINESS_THU,BUSINESS_FRI,BUSINESS_SAT,BUSINESS_SUN
                6434b20bb62491116a7b1acd,South Lyon,796 Hillcrest Ave,48178,Los Angeles,California,United States,12.7,34.5,https://static.nike.com/image.jpg,+12029182132,America/Los_Angeles,10:00AM-01:00PM,,,,,,
                """;

        assertEquals(expectedCsv, write.toString());
    }

    @Test
    void convertToCsvEmptyList() {
        Writer write = csvWriter.write(new ArrayList<>(), new StringWriter());

        System.out.println(write);

        assertEquals("""
                LOCATION_ID,LOCATION_NAME,ADDRESS_STREET,ADDRESS_ZIP_CODE,ADDRESS_CITY,ADDRESS_REGION,COUNTRY_NAME,LATITUDE,LONGITUDE,IMAGE_URL,CONTACT_PHONE,TIMEZONE,BUSINESS_MON,BUSINESS_TUE,BUSINESS_WED,BUSINESS_THU,BUSINESS_FRI,BUSINESS_SAT,BUSINESS_SUN
                """, write.toString());
    }
}
