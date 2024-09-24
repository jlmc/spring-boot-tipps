package io.github.jlmc.poc;

import io.github.jlmc.poc.st.CreateOrderIT;
import io.github.jlmc.poc.st.ResultLoggerExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.WireMockContainer;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
@ExtendWith({ResultLoggerExtension.class})
class OpenfeignResilientCustomisationsApplicationIT {

    //@formatter:off
    @Container
    static WireMockContainer wiremockServer =
            new WireMockContainer(Containers.WIREMOCK)
                .withFileFromResource("product-by-1.json", "/wiremock/__files/product-by-1.json")
                .withFileFromResource("product-by-5.json", "/wiremock/__files/product-by-5.json")
                .withFileFromResource("order-id.json", "/wiremock/__files/order-id.json")
                .withMappingFromResource("products-service-api.json", CreateOrderIT.class, "/wiremock/mappings/products-service-api.json")
                .withMappingFromResource("order-id-generator-service-api.json", CreateOrderIT.class, "/wiremock/mappings/order-id-generator-service-api.json")
                .withBanner();
    //@formatter:on

    //@formatter:off
    @SuppressWarnings("resource")
    @Container
    static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>(Containers.POSTGRES)
                    .withDatabaseName("poc");
    //@formatter:on

    @Autowired
    private ApplicationContext context;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.openfeign.client.config.products-service-client.url", wiremockServer::getBaseUrl);
        registry.add("spring.cloud.openfeign.client.config.order-id-generator-service-client.url", wiremockServer::getBaseUrl);

        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Test
    void contextLoads() {
        assertNotNull(context);
    }

}