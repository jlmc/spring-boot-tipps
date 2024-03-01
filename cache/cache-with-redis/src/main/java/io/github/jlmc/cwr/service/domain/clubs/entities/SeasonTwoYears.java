package io.github.jlmc.cwr.service.domain.clubs.entities;

import java.io.Serial;
import java.io.Serializable;
import java.time.Year;
import java.util.Objects;

public class SeasonTwoYears implements Season, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Year start;
    private final Year end;

    private SeasonTwoYears(Year start, Year end) {
        this.start = start;
        this.end = end;
    }

    public Year getStart() {
        return start;
    }

    public Year getEnd() {
        return end;
    }

    public static SeasonTwoYears of(Year start, Year end) {
        Objects.requireNonNull(start);
        Objects.requireNonNull(end);
        return new SeasonTwoYears(start, end);
    }

    @Override
    public String toString() {
        return title();
    }

    @Override
    public String title() {
        return "%s-%s".formatted(start, end);
    }
}
