package io.github.jlmc.cwr.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.time.*;

@Testcontainers
public class AbstractIT {

    @Container
    static final DockerComposeContainer<?> DOCKER_COMPOSE_CONTAINER =
            new DockerComposeContainer<>(new File("docker-compose-it.yaml"))
                    .withExposedService("postgres", 5432)
                    .withExposedService("redis", 6379)
                    .withExposedService("redis-sentinel", 26379)
                    .withRemoveVolumes(true)

            ;

    @Configuration
    public static class MockConfiguration {
        @Bean
        @Primary
        Clock fixedClock() {
            Instant instant =
                    LocalDate.parse("2024-01-01")
                            .atStartOfDay()
                            .toInstant(ZoneOffset.UTC);
            return Clock.fixed(instant, ZoneId.of("UTC"));
        }
    }
}
