package io.github.jlmc.uploadcsv.domain;

import io.github.jlmc.uploadcsv.ObjectFactory;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static com.mongodb.assertions.Assertions.assertTrue;
import static io.github.jlmc.uploadcsv.ObjectFactory.slot;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

        assertEquals(
                Map.of(
                        DayOfWeek.MONDAY, Set.of(Slot.of(LocalTime.parse("09:00"), LocalTime.parse("19:00"))),
                        DayOfWeek.FRIDAY, Set.of(Slot.of(LocalTime.parse("09:00"), LocalTime.parse("19:00"))),
                        DayOfWeek.THURSDAY, Set.of(Slot.of(LocalTime.parse("09:00"), LocalTime.parse("19:00"))),
                        DayOfWeek.SUNDAY, Set.of(Slot.of(LocalTime.parse("09:00"), LocalTime.parse("19:00"))),
                        DayOfWeek.TUESDAY, Set.of(Slot.of(LocalTime.parse("09:00"), LocalTime.parse("19:00"))),
                        DayOfWeek.WEDNESDAY, Set.of(Slot.of(LocalTime.parse("09:00"), LocalTime.parse("19:00"))),
                        DayOfWeek.SATURDAY, Set.of(Slot.of(LocalTime.parse("09:00"), LocalTime.parse("19:00")))
                )
                , openHours
        );

        System.out.println(dailyAvailabilities);
        // [DailyAvailabilities[date=2022-04-30, slots=[Slot(openAt=09:00, closeAt=19:00)], specialDayName=null], DailyAvailabilities[date=2022-05-01, slots=[Slot(openAt=05:00, closeAt=23:00)], specialDayName=Pope Visit], DailyAvailabilities[date=2022-05-02, slots=[Slot(openAt=09:00, closeAt=19:00)], specialDayName=null], DailyAvailabilities[date=2022-05-03, slots=[Slot(openAt=09:00, closeAt=19:00)], specialDayName=null], DailyAvailabilities[date=2022-05-04, slots=[Slot(openAt=09:00, closeAt=19:00)], specialDayName=null], DailyAvailabilities[date=2022-05-05, slots=[Slot(openAt=09:00, closeAt=19:00)], specialDayName=null], DailyAvailabilities[date=2022-05-06, slots=[Slot(openAt=09:00, closeAt=19:00)], specialDayName=null]]

        assertFalse(dailyAvailabilities.isEmpty());
        assertEquals(DayOfWeek.values().length, dailyAvailabilities.size());
        assertEquals(
                new DailyAvailabilities(
                        LocalDate.parse("2022-04-30"),
                        List.of(Slot.of(LocalTime.parse("09:00"), LocalTime.parse("19:00"))),
                        null
                ), dailyAvailabilities.get(0));
        assertEquals(
                new DailyAvailabilities(
                        LocalDate.parse("2022-05-01"),
                        List.of(Slot.of(LocalTime.parse("05:00"), LocalTime.parse("23:00"))),
                        "Pope Visit"
                ), dailyAvailabilities.get(1));

    }

}
