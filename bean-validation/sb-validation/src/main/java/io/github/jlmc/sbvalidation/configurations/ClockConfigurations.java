package io.github.jlmc.sbvalidation.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ClockConfigurations {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
