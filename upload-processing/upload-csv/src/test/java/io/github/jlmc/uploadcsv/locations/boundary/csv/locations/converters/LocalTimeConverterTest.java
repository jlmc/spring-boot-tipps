package io.github.jlmc.uploadcsv.locations.boundary.csv.locations.converters;

import com.opencsv.exceptions.CsvConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LocalTimeConverterTest {

    private final LocalTimeConverter sut = LocalTimeConverter.INSTANCE;

    @Test
    @DisplayName("when we parse time in the ISO-8601 24H format, it converts to the valid local time")
    void parse24FormatTest() throws CsvConstraintViolationException {
        assertEquals(LocalTime.parse("20:00"), sut.convert("20:00"));
        assertEquals(LocalTime.parse("12:00"), sut.convert("12:00"));
        assertEquals(LocalTime.parse("00:30"), sut.convert("00:30"));
        assertEquals(LocalTime.parse("23:59:59"), sut.convert("23:59:59"));
    }

    @Test
    @DisplayName("when we parse time in the 12H format in PM with just the hours, it converts to the valid local time")
    void parsePMJustHoursFormatTest() throws CsvConstraintViolationException {
        assertEquals(LocalTime.parse("00:00"), sut.convert("00am"));
        assertEquals(LocalTime.parse("01:00"), sut.convert("01am"));
        assertEquals(LocalTime.parse("11:00"), sut.convert("11AM"));
        assertEquals(LocalTime.parse("12:00"), sut.convert("00pm"));
        assertEquals(LocalTime.parse("12:00"), sut.convert("00PM"));
        assertEquals(LocalTime.parse("13:00"), sut.convert("01PM"));
        assertEquals(LocalTime.parse("23:00"), sut.convert("11PM"));
        assertEquals(LocalTime.parse("00:00"), sut.convert("0AM"));
        assertEquals(LocalTime.parse("01:00"), sut.convert("1AM"));
        assertEquals(LocalTime.parse("13:00"), sut.convert("1PM"));
        assertEquals(LocalTime.parse("23:00"), sut.convert("11PM"));
    }

    @Test
    @DisplayName("when we parse time in the 12H format in PM, it converts to the valid local time")
    void parsePMFormatTest() throws CsvConstraintViolationException {
        assertEquals(LocalTime.parse("13:30"), sut.convert("01:30pm"));
        assertEquals(LocalTime.parse("13:30"), sut.convert("01:30PM"));
        assertEquals(LocalTime.parse("12:00"), sut.convert("00:00PM"));
        assertEquals(LocalTime.parse("23:59"), sut.convert("11:59PM"));
    }

    @Test
    @DisplayName("when we parse time in the 12H format in AM, it converts to the valid local time")
    void parseAMFormatTest() throws CsvConstraintViolationException {
        assertEquals(LocalTime.parse("01:30"), sut.convert("01:30am"));
        assertEquals(LocalTime.parse("01:30"), sut.convert("01:30AM"));
        assertEquals(LocalTime.parse("00:00"), sut.convert("00:00AM"));
        assertEquals(LocalTime.parse("11:59"), sut.convert("11:59AM"));
        assertEquals(LocalTime.parse("11:59"), sut.convert("11:59AM"));
    }

    @Test
    @DisplayName("when we parse time with invalid format, it throw a CsvConstraintViolationException")
    void parseInvalidInputTest() {
        var exception =
                assertThrows(CsvConstraintViolationException.class, () -> sut.convert("26:61"));
        assertEquals("The value 26:61 is not a valid local time.", exception.getMessage());
    }

}
