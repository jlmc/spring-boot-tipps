package io.github.jlmc.poc.st;

import io.github.jlmc.poc.Containers;
import io.github.jlmc.poc.adapters.auditing.control.ExternalRequestAuditRepository;
import io.github.jlmc.poc.api.invoices.outputs.InvoiceRepresentation;
import io.github.jlmc.poc.pdf.PdfMerger;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.WireMockContainer;

import java.io.IOException;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
@ExtendWith({ResultLoggerExtension.class})
public class InvoicesIT {


    @SuppressWarnings("resource")
    @Container
    static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>(Containers.POSTGRES)
                    .withDatabaseName("poc");

    private static final Logger LOGGER = LoggerFactory.getLogger(InvoicesIT.class);

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
    @Autowired
    ExternalRequestAuditRepository externalRequestAuditRepository;
    @LocalServerPort
    private Integer port;

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
    void when_get_invoice_successfully_it_returns_200() throws IOException {
        LOGGER.info("Testing get invoice order successfully");

        byte[][] fileContents =
                Stream.of("1", "2", "999", "4", "x")
                        .map(this::getInvoice)
                        .map(InvoiceRepresentation::fileContent)
                        .toArray(byte[][]::new);

        byte[] mergedPdfBytes = PdfMerger.mergePDFs(fileContents);

        Assertions.assertNotNull(mergedPdfBytes);

        //PdfMerger.saveBytesToFile(mergedPdfBytes, Paths.get("all-invoices-" + Instant.now() + ".pdf"));
        //new File("merged_output.pdf").delete();
    }

    InvoiceRepresentation getInvoice(String number) {
        //@formatter:off
        return given()
                .log().all() // Log the full request (headers, body, etc.)
         .when()
            .get("/api/invoices/{number}", number)
         .then()
            .log().all()  // Log the full response (headers, body, etc.)
            .statusCode(200)
            .contentType(ContentType.JSON)
            .extract()
            .as(InvoiceRepresentation.class); // Extract response and map to User class;
        //@formatter:on
    }


}
