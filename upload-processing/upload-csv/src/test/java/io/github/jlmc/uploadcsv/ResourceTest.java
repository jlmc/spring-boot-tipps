package io.github.jlmc.uploadcsv;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import io.github.jlmc.uploadcsv.locations.control.CsvReadResult;
import io.github.jlmc.uploadcsv.locations.control.CsvReader;
import io.github.jlmc.uploadcsv.locations.control.CsvWriter;
import io.github.jlmc.uploadcsv.locations.entity.Address;
import io.github.jlmc.uploadcsv.locations.entity.Coordinates;
import io.github.jlmc.uploadcsv.locations.entity.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ResourceTest {

    String text = """
            "ID","NAME","PHONENUMBER","IMAGEURL","TIMEZONE","ADDRESS_ADDRESS","ADDRESS_CITY","ADDRESS_ZIP_CODE","ADDRESS_COUNTRY_NAME","ADDRESS_REGION_NAME","LATITUDE","LONGITUDE","BUSINESS_MON"
            "1","forum","123","image-1.png","Europe/Lisbon","Rua do Alto","Santa clara","3100","Portugal","Coimbra","12.0","99.0",""
            "2","columbo","125","image-2.png","Europe/Lisbon","Oriente","Lisboa","3000","Portugal","Lisboa","17.093273892731","98.12345678",""
            "","columbo","125","image-3.png","Europe/Lisbon","Oriente","Lisboa","3000","Portugal","Lisboa","18.12","76.34",""
            """;
    CsvReader csvReader = new CsvReader();
    CsvWriter csvWriter = new CsvWriter();

    List<Location> locations = new ArrayList<>();

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

        locations = List.of(location1);
    }

    @Test
    void beansToCSV() throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
        Writer writer = csvWriter.write(locations, new StringWriter());

        System.out.println(writer);
    }

    @Test
    void csvToBeans() {
        var result = csvReader.read(new StringReader(text));

        System.out.println(result.items());

        assertEquals(3, result.items().size());
    }

    @Test
    void writeAndRead() {
       var result = csvReader.read(new StringReader(text));

        Writer write = csvWriter.write(result.items(), new StringWriter());
        String s = write.toString();
        System.out.println(s);

        assertEquals(text, s);
    }

    @Test
    void readValidation() {
        String inputWithErrors =
                """
                ID,NAME,PHONENUMBER,IMAGEURL,TIMEZONE,ADDRESS_ADDRESS,ADDRESS_CITY,ADDRESS_ZIP_CODE,ADDRESS_COUNTRY_NAME,ADDRESS_REGION_NAME,LATITUDE,LONGITUDE
                1,forum,123,image-1.png,Europe/Lisbon,Rua do Alto,Santa clara,3100,Portugal,Coimbra,12.0,99.0
                2,columbo,125,image-2.png,Europe/Lisbon,Oriente,Lisboa,3000,Portugal,Lisboa,17.093273892731,98.12345678
                ,,125,image-3.png,Europe/Lisbon,Oriente,Lisboa,3000,Portugal,Lisboa,18.12,invalid-longitude
                ,columbo,125,image-3.png,Europe/Lisbon,Oriente,Lisboa,3000,Portugal,Lisboa,18.12,76.34
                ,,125,image-3.png,Europe/Lisbon,Oriente,Lisboa,3000,Portugal,Lisboa,18.12,76.34
                ,test5
                """;

        CsvReadResult<Location> result = csvReader.read(new StringReader(inputWithErrors));

        assertNotNull(result);
        assertNotNull(result.items());
        assertFalse(result.isValid());
    }

    @Test
    void customMapper() {
        String csv = """
                "ID","NAME","PHONENUMBER","IMAGEURL","TIMEZONE","ADDRESS_ADDRESS","ADDRESS_CITY","ADDRESS_ZIP_CODE","ADDRESS_COUNTRY_NAME","ADDRESS_REGION_NAME","LATITUDE","LONGITUDE","BUSINESS_MON"
                "1","forum","123","image-1.png","Europe/Lisbon","Rua do Alto","Santa clara","3100","Portugal","Coimbra","12.0","99.0","10:00am-01:00pm | 02:00pm-08pm"
                """;

        CsvReadResult<Location> result = csvReader.read(new StringReader(csv));

        assertTrue(result.isValid());
        assertEquals(1, result.items().size());
        System.out.println(result);


        Writer write = csvWriter.write(result.items(), new StringWriter());

        System.out.println(write);

        String expectedCsv = """
                "ID","NAME","PHONENUMBER","IMAGEURL","TIMEZONE","ADDRESS_ADDRESS","ADDRESS_CITY","ADDRESS_ZIP_CODE","ADDRESS_COUNTRY_NAME","ADDRESS_REGION_NAME","LATITUDE","LONGITUDE","BUSINESS_MON"
                "1","forum","123","image-1.png","Europe/Lisbon","Rua do Alto","Santa clara","3100","Portugal","Coimbra","12.0","99.0","10:00AM-01:00PM|02:00PM-08:00PM"
                """;

        assertEquals(expectedCsv, write.toString());
    }

    @Test
    void convertToCsvEmptyList() {
        Writer write = csvWriter.write(new ArrayList<>(), new StringWriter());

        System.out.println(write);
    }
}
