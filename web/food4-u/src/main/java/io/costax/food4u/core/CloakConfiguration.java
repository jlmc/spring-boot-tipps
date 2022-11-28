package io.costax.food4u.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class CloakConfiguration {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

}
