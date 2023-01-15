package io.github.jlmc.sbvalidation;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Configuration
public class MockClockConfigurations {

    @Configuration
    public class ClockConfigurations {

        @Primary
        @Bean(name = "mockClock")
        public Clock clock() {

            ZoneId zoneId = ZoneId.of("Europe/Lisbon");
            Instant instant =
                    LocalDate.parse("2023-01-01")
                             .atTime(21, 30, 1)
                             .atZone(zoneId)
                             .toInstant();

            return Clock.fixed(instant, zoneId);
        }
    }
}
