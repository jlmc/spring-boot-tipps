package io.github.jlmc.uploadcsv.locations.entity;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LocalTimeConverterTest {

    private final LocalTimeConverter localTimeConverter = new LocalTimeConverter();

    @Test
    void parseInvalidInput() {
        var exception =
                assertThrows(CsvDataTypeMismatchException.class, () -> localTimeConverter.convertToRead("26:61"));

        assertEquals("The value 26:61 is not a valid local time", exception.getMessage());
    }

    @Test
    void parse24Format() throws CsvDataTypeMismatchException {
        assertEquals(LocalTime.parse("20:00"), localTimeConverter.convertToRead("20:00"));
        assertEquals(LocalTime.parse("12:00"), localTimeConverter.convertToRead("12:00"));
    }

    @Test
    void parsePartFormat() throws CsvDataTypeMismatchException {
        assertEquals(LocalTime.parse("20:00"), localTimeConverter.convertToRead("08pm"));
    }

    @Test
    void parsePMFormat() throws CsvDataTypeMismatchException {
        assertEquals(LocalTime.parse("13:30"), localTimeConverter.convertToRead("01:30pm"));
        assertEquals(LocalTime.parse("13:30"), localTimeConverter.convertToRead("01:30PM"));
    }

    @Test
    void parseAMFormat() throws CsvDataTypeMismatchException {
        assertEquals(LocalTime.parse("10:00"), localTimeConverter.convertToRead("10:00am"));
        assertEquals(LocalTime.parse("10:00"), localTimeConverter.convertToRead("10:00AM"));
    }
}
