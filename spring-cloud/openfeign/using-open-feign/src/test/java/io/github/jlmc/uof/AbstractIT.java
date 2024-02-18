package io.github.jlmc.uof;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext
@ActiveProfiles("test")
public class AbstractIT {

    public static final int WIREMOCK_DEFAULT_PORT = 8080;
    @Container
    public static GenericContainer wiremock =
            new GenericContainer("wiremock/wiremock:3.3.1")
                    //.withAccessToHost(true)
                    .withExposedPorts(WIREMOCK_DEFAULT_PORT)
                    .withFileSystemBind(
                            "./src/test/resources/wiremock/wm1",
                            "/home/wiremock",
                            BindMode.READ_ONLY)
                    .withCommand("/docker-entrypoint.sh", "--global-response-templating", "--disable-gzip", "--verbose")
            ;
           // DockerImageName.parse(getTestContainersRabbitImageName()).asCompatibleSubstituteFor("rabbitmq"));

    @SuppressWarnings("unused")
    @DynamicPropertySource
    static void initProperties(final DynamicPropertyRegistry registry) {
        registry.add("test.wiremock.host", wiremock::getHost);
        registry.add("test.wiremock.port", () -> wiremock.getMappedPort(WIREMOCK_DEFAULT_PORT));

//        registry.add("spring.cloud.openfeign.client.config.marketplaceRequester.url", () -> getMarketplaceUrl());
    }
}
