package io.github.jlmc.cwr.service.domain.clubs.entities;

import java.time.Year;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface Season {
    String title();

    static Season valueOf(String title) {
        String[] split = title.split("-");

        List<Year> years = Arrays.stream(split).map(Year::parse).toList();

        var mapper = MAP_INSTANCE.get(years.size());

        if (mapper == null) {
            throw new IllegalArgumentException("Can not convert %s in to a Season implementation".formatted(title));
        }

        return mapper.apply(years);
    }

    static final Map<Integer, Function<List<Year>, Season>> MAP_INSTANCE =
            Map.of(
                    1, l -> SeasonOneYear.of(l.getFirst()),
                    2, l -> SeasonTwoYears.of(l.getFirst(), l.get(1))
            );


}
