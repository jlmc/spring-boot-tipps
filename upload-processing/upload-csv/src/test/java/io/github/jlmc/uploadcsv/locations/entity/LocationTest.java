package io.github.jlmc.uploadcsv.locations.entity;

import io.github.jlmc.uploadcsv.ObjectFactory;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static com.mongodb.assertions.Assertions.assertTrue;
import static io.github.jlmc.uploadcsv.ObjectFactory.slot;
import static org.junit.jupiter.api.Assertions.assertFalse;

class LocationTest {

    @Test
    void imageUrlRegex() {
        String regex = Location.IMAGE_URL_REGEX;
        Pattern pattern = Pattern.compile(regex);

        assertTrue(pattern.matcher("https://static.nike.com/a/images/f_auto/c5fbda6e-5cb6-4fae-8a05-e47212074dbf/image.jpeg").matches());
        assertTrue(pattern.matcher("https://static.nike.com/image.jpeg").matches());
        assertTrue(pattern.matcher("https://static.nike.com/image.png").matches());
        assertTrue(pattern.matcher("https://static.nike.com/image.jpg").matches());
        assertTrue(pattern.matcher("https://static.nike.com/image.webp").matches());
        assertFalse(pattern.matcher("https://static.nike.com/image.vec").matches());
    }

    @Test
    void nextOpenNDaysBusiness() {
        Location location = ObjectFactory.location().toBuilder()
                                         .specialDays(Set.of(
                                                         SpecialDay.yearly(LocalDate.parse("2021-05-01"), "Worker day", "", Set.of(
                                                                 slot("07:00", "15:00")
                                                         )),
                                                         SpecialDay.single(LocalDate.parse("2021-05-24"), "team build", "", Set.of(
                                                                 slot("07:00", "15:00")
                                                         )),
                                                         SpecialDay.single(LocalDate.parse("2022-05-01"), "Pope Visit", "", Set.of(
                                                                 slot("05:00", "23:00")
                                                         ))
                                                 )
                                         )


                                         .build();

        Map<DayOfWeek, Set<Slot>> openHours = location.getOpenHours();


        var instant = LocalDate.parse("2022-05-01").atStartOfDay(ZoneId.of("UTC")).toInstant();

        List<DailyAvailabilities> dailyAvailabilities = location.dailyAvailabilitiesOf(instant, 7);

        System.out.println(openHours);


    }

}
