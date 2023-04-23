package io.github.jlmc.uploadcsv.locations.boundary.csv.locations.converters;

import com.opencsv.exceptions.CsvConstraintViolationException;
import io.github.jlmc.uploadcsv.locations.boundary.csv.locations.model.SlotCsvResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("when we convert an csv slot text to a list of slots")
public class SlotCsvResourceListConverterTest {

    private final SlotCsvResourceListConverter sut = new SlotCsvResourceListConverter();

    static List<String> blankTexts() {
        ArrayList<String> strings = new ArrayList<>();
        strings.add(" ");
        strings.add("");
        strings.add(null);
        return strings;
    }

    @Test
    @DisplayName("it converts successful")
    void convertTextToListCsvSlots() throws CsvConstraintViolationException {
        var csvSlots = sut.convert("10:00am-01:00pm | 02:00pm-08pm");

        assertEquals(2, csvSlots.size());
        assertNotNull(csvSlots.get(0));
        assertNotNull(csvSlots.get(1));
        assertEquals(new SlotCsvResource(LocalTime.of(10, 0), LocalTime.of(13, 0)), csvSlots.get(0));
        assertEquals(new SlotCsvResource(LocalTime.of(14, 0), LocalTime.of(20, 0)), csvSlots.get(1));
    }

    @DisplayName("when the text is blank it returns a empty slot list")
    @ParameterizedTest(name = ParameterizedTest.INDEX_PLACEHOLDER + " - when the text is <{0}>")
    @MethodSource("blankTexts")
    void convertBlankText(String text) throws CsvConstraintViolationException {
        var slots = sut.convert(text);
        assertTrue(slots.isEmpty());
    }

    @Test
    @DisplayName("it throws an exception when the period do not contains the 2 required open and close elements")
    void convertTextWithoutTheTwoRequiredStatAndCloseElements() {
        String input = "10:00am";
        var exception =
                assertThrows(CsvConstraintViolationException.class, () -> sut.convert(input));
        assertEquals("The csv slots periods must have a start and a close element, the value " + input + " can be parsed to a Slot.", exception.getMessage());
    }

    @Test
    @DisplayName("it throws a exception when the period have more than 2 elements")
    void convertTextWithMoreTheTwoAdmittedStatAndCloseElements() {
        var input = "10:00am-01:00pm-15:00";

        var exception =
                assertThrows(CsvConstraintViolationException.class, () -> sut.convert(input));

        assertEquals("The csv slots periods must have a start and a close element, the value " + input + " can be parsed to a Slot.", exception.getMessage());
    }
}
