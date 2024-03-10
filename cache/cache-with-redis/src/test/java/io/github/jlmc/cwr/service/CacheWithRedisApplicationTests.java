package io.github.jlmc.cwr.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;

@Testcontainers
@SpringBootTest
class CacheWithRedisApplicationTests {

    @Container
    static final DockerComposeContainer<?> DOCKER_COMPOSE_CONTAINER =
            new DockerComposeContainer<>(new File("docker-compose-it.yaml"))
                    .withExposedService("postgres", 5432)
                    .withExposedService("redis", 6379)
                    .withExposedService("redis-sentinel", 26379)
                    .withRemoveVolumes(true)

            ;

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheWithRedisApplicationTests.class);

    @Autowired
    ApplicationContext context;

    @Test
    void contextLoads() {
        LOGGER.info("context loaded");
        Assertions.assertNotNull(context);
    }

    @AfterAll
    static void afterAll() {
        LOGGER.info("test shutdown");
        DOCKER_COMPOSE_CONTAINER.stop();
    }
}
