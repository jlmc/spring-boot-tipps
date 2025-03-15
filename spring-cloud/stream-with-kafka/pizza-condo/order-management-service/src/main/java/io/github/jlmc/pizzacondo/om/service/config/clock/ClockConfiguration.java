package io.github.jlmc.pizzacondo.om.service.config.clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ClockConfiguration {

    @Bean
    Clock clock() {
        return Clock.systemDefaultZone();
    }
}

