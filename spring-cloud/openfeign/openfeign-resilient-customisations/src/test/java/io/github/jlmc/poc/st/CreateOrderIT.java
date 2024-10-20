package io.github.jlmc.poc.st;

import io.github.jlmc.poc.Containers;
import io.github.jlmc.poc.adapters.auditing.entity.ExternalRequestAudit;
import io.github.jlmc.poc.adapters.auditing.control.ExternalRequestAuditRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.WireMockContainer;

import java.util.List;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
@ExtendWith({ResultLoggerExtension.class})
public class CreateOrderIT {

    private static final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
    private static final Pattern UUID_REGEX_PATTERN = Pattern.compile(UUID_REGEX);
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateOrderIT.class);

    //@formatter:off
    @Container
    static WireMockContainer wiremockServer =
            new WireMockContainer(Containers.WIREMOCK)
                .withFileFromResource("product-by-1.json", "/wiremock/__files/product-by-1.json")
                .withFileFromResource("product-by-5.json", "/wiremock/__files/product-by-5.json")
                .withFileFromResource("order-id.json", "/wiremock/__files/order-id.json")
                .withMappingFromResource("products-service-api.json", CreateOrderIT.class, "/wiremock/mappings/products-service-api.json")
                .withFileFromResource("invoice-x.json", "/wiremock/__files/invoice-x.json")
                .withMappingFromResource("order-id-generator-service-api.json", CreateOrderIT.class, "/wiremock/mappings/order-id-generator-service-api.json")
                .withMappingFromResource("exodus-api.json", InvoicesIT.class, "/wiremock/mappings/exodus-api.json")
                .withBanner()
            ;
    //@formatter:on

    //@formatter:off
    @SuppressWarnings("resource")
    @Container
    static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>(Containers.POSTGRES)
                    .withDatabaseName("poc");
    //@formatter:on

    @LocalServerPort
    private Integer port;

    @Autowired
    ExternalRequestAuditRepository externalRequestAuditRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.openfeign.client.config.products-service-client.url", wiremockServer::getBaseUrl);
        registry.add("spring.cloud.openfeign.client.config.order-id-generator-service-client.url", wiremockServer::getBaseUrl);
        registry.add("spring.cloud.openfeign.client.config.exodus-service-client.url", wiremockServer::getBaseUrl);

        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @BeforeEach
    void setUp() {
        LOGGER.info("X -> App Port: [{}]", port);
        RestAssured.port = port;
    }

    @Test
    void when_create_order_successfully_it_returns_201() {
        LOGGER.info("Testing create order successfully");

        @Language("JSON") String jsonBody = """
                {
                  "items": [
                    {
                      "productId": "1",
                      "quantity": 1
                    }
                  ]
                }
                """;

        //@formatter:off
        given()
                .log().all() // Log the full request (headers, body, etc.)
                .contentType(ContentType.JSON)
                .body(jsonBody)
        .when()
                .post("/api/orders")
        .then()
                .log().all()  // Log the full response (headers, body, etc.)
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("id", matchesPattern(UUID_REGEX_PATTERN));  // Assert that 'id' matches UUID regex
        //@formatter:on
    }

    @Test
    void when_create_order_with_retry_fetch_product_details_successfully_it_returns_201() {
        LOGGER.info("testing retry fetch product details");

        @Language("JSON") String jsonBody = """
                {
                  "items": [
                    {
                      "productId": "5",
                      "quantity": 3
                    }
                  ]
                }
                """;

        //@formatter:off
        given()
                .log().all() // Log the full request (headers, body, etc.)
                .contentType(ContentType.JSON)
                .body(jsonBody)
        .when()
                .post("/api/orders")
        .then()
                .log().all()  // Log the full response (headers, body, etc.)
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("id", matchesPattern(UUID_REGEX_PATTERN));  // Assert that 'id' matches UUID regex
        //@formatter:on

        List<ExternalRequestAudit> all = externalRequestAuditRepository.findAll();
        assertEquals(1,
                all.stream()
                        .map(it -> "%s %s".formatted(it.getHttpMethod(), it.getUrl()))
                        .distinct()
                        .count()
        );
        assertEquals(3,
                all.stream()
                        .filter(it -> it.getResponseStatus() >= 500)
                        .count()
        );
        assertEquals(1,
                all.stream()
                        .filter(it -> it.getResponseStatus() == 200)
                        .count()
        );

    }

    @Test
    void when_create_order_of_a_non_existing_product() {
        LOGGER.info("Testing create order of a non existing product");

        @Language("JSON") String jsonBody = """
                {
                  "items": [
                    {
                      "productId": "2",
                      "quantity": 1
                    }
                  ]
                }
                """;

        //@formatter:off
        given()
                .log().all() // Log the full request (headers, body, etc.)
                .contentType(ContentType.JSON)
                .body(jsonBody)
        .when()
                .post("/api/orders")
        .then()
                .log().all()  // Log the full response (headers, body, etc.)
                .statusCode(HttpStatus.PRECONDITION_FAILED.value())
                .contentType(ContentType.JSON)
                .body(JsonMatches.jsonEqualsTo(
                        """
                            {
                              "type": "about:blank",
                              "title": "Precondition Failed",
                              "status": 412,
                              "detail": "Product with the id [2] not found.",
                              "instance": "/api/orders"
                            }
                            """, false
                ));
        //@formatter:on
    }



}
