package io.github.jlmc.uploadcsv.locations.boundary.csv.locations.converters;

import com.opencsv.exceptions.CsvConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CsvZoneIdConverterTest {

    private final CsvZoneIdConverter sut = new CsvZoneIdConverter();

    static List<String> validTimeZones() {
        return ZoneId.getAvailableZoneIds().stream().toList();
    }

    @ParameterizedTest(name = ParameterizedTest.INDEX_PLACEHOLDER + " - when the timeZone is <{0}> should be invalid")
    @ValueSource(strings = {"", " ", "invalid-zone-id"})
    void invalidValues(String timeZone) {
        var exception = assertThrows(CsvConstraintViolationException.class, () -> sut.convert(timeZone));

        assertEquals("'" + timeZone + "' is not in the zones list defined by IANA.", exception.getMessage());
    }

    @ParameterizedTest(name = ParameterizedTest.INDEX_PLACEHOLDER + " - when the timeZone is <{0}> should be valid")
    @MethodSource("validTimeZones")
    void validValue(String timeZone) throws CsvConstraintViolationException {
        assertEquals(ZoneId.of(timeZone), sut.convert(timeZone));
    }

    @Test
    @DisplayName("when the timeZone is <null> should return null")
    void nullValue() throws CsvConstraintViolationException {
        assertNull(sut.convert(null));
    }

}
