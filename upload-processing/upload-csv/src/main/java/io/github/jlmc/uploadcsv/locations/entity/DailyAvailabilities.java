package io.github.jlmc.uploadcsv.locations.entity;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public record DailyAvailabilities(LocalDate date, List<Slot> slots, String specialDayName) {

    public DayOfWeek dayOfWeek() {
        return date.getDayOfWeek();
    }

    public DailyAvailabilities(LocalDate date, List<Slot> slots) {
        this(date, slots, null);
    }
}
