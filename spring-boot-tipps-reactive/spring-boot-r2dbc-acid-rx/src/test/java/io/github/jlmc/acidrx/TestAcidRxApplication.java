package io.github.jlmc.acidrx;

import org.junit.jupiter.api.Disabled;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

@Disabled
@TestConfiguration(proxyBeanMethods = false)
public class TestAcidRxApplication {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>("postgres:latest");
    }

    public static void main(String[] args) {
        SpringApplication.from(AcidRxApplication::main).with(TestAcidRxApplication.class).run(args);
    }

}
