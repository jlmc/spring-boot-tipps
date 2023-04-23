package io.github.jlmc.uploadcsv.locations.entity;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

public record SpecialDay(
        LocalDate day,
        String name,
        String note,
        SpecialDayRepeatsType repeats,
        Set<Slot> slots
) {

    private SpecialDay(LocalDate day, String name, String note, SpecialDayRepeatsType repeats) {
        this(day, name, note, repeats, null);
    }

    public static SpecialDay single(LocalDate day, String name, String note, Set<Slot> slots) {
        return specialDayOf(slots, day, name, note, SpecialDayRepeatsType.SINGLE);
    }


    public static SpecialDay monthly(LocalDate day, String name, String note, Set<Slot> slots) {
        return specialDayOf(slots, day, name, note, SpecialDayRepeatsType.MONTHLY);
    }


    public static SpecialDay yearly(LocalDate day, String name, String note, Set<Slot> slots) {
        return specialDayOf(slots, day, name, note, SpecialDayRepeatsType.YEARLY);
    }

    private static SpecialDay specialDayOf(Set<Slot> slots, LocalDate day, String name, String note, SpecialDayRepeatsType repeatsType) {
        Objects.requireNonNull(slots);
        Objects.requireNonNull(day);
        Objects.requireNonNull(name);
        Objects.requireNonNull(name);
        return new SpecialDay(day, name, note, repeatsType, Set.copyOf(slots));
    }

    public boolean match(LocalDate comparingDate) {
        return this.repeats.match(this.day, comparingDate);
    }
}
