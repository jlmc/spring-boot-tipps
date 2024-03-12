package io.github.jlmc.cwr.service.domain.clubs.entities;

import org.hibernate.Session;

import java.io.Serial;
import java.io.Serializable;
import java.time.Year;

public class SeasonOneYear implements Season, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Year year;

    private SeasonOneYear(Year year) {
        this.year = year;
    }

    public static SeasonOneYear of(Year year) {
        return new SeasonOneYear(year);
    }

    public Year getYear() {
        return year;
    }

    @Override
    public String toString() {
        return title();
    }

    @Override
    public String title() {
        return "%s".formatted(year);
    }
}
