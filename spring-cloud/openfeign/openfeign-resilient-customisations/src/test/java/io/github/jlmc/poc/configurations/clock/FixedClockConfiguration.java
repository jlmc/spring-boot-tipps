package io.github.jlmc.poc.configurations.clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.*;

@Configuration
public class FixedClockConfiguration {

    @Bean
    @Primary
    Clock fixedClock() {
        ZoneId europeLisbon = ZoneId.of("Europe/Lisbon");
        ZoneId utc = ZoneId.of("UTC");

        ZonedDateTime zonedDateTime =
                LocalDate.of(2024, Month.SEPTEMBER, 12).atTime(13, 31, 54)
                        .atZone(europeLisbon);

        return Clock.fixed(zonedDateTime.toInstant(), utc);
    }

}
