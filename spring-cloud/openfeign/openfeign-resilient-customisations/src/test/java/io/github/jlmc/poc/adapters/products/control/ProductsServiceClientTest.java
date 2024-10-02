package io.github.jlmc.poc.adapters.products.control;

import io.github.jlmc.poc.configurations.clock.FixedClockConfiguration;
import io.github.jlmc.poc.configurations.openfeign.OpenFeignConfiguration;
import io.github.jlmc.poc.configurations.openfeign.logger.AuditRequestLog;
import io.github.jlmc.poc.configurations.openfeign.logger.AuditResponseLog;
import io.github.jlmc.poc.configurations.openfeign.logger.Auditor;
import io.github.jlmc.poc.domain.orders.entities.Product;
import okhttp3.Headers;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {
        OpenFeignConfiguration.class,
        ProductsServiceClientConfiguration.class,
        ProductsServiceClient.class
})
@Import({
        JacksonAutoConfiguration.class,
        HttpMessageConvertersAutoConfiguration.class,
        FeignAutoConfiguration.class,
        FixedClockConfiguration.class
})
class ProductsServiceClientTest {

    private static MockWebServer mockWebServer;

    @Autowired
    ProductsServiceClient productsServiceClient;

    @MockBean
    Auditor auditor;

    @Captor
    ArgumentCaptor<AuditRequestLog> auditRequestLogCaptor;

    @Captor
    ArgumentCaptor<AuditResponseLog> auditResponseLogCaptor;

    @BeforeAll
    static void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        //mockWebServer.start(InetAddress.getByAddress("localhost".getBytes()), 8383);
        mockWebServer.start();
    }

    @AfterAll
    static void afterAll() throws Exception {
        mockWebServer.shutdown();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        System.out.println("Setting up properties");
        registry.add("spring.cloud.openfeign.client.config.products-service-client.url", () -> mockWebServer.url("").toString());
    }

    @Test
    void when_get_product_by_id_it_makes_the_request_with_expected_method_and_headers() throws InterruptedException {
        // Arrange: Mock response from the server
        final String responseBody = """
                {
                  "id": 25,
                  "name": "John Doe",
                  "description": "Some description"
                }
                """;
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBodyDelay(100, TimeUnit.MILLISECONDS)
                .setBody(responseBody)
                .addHeader("Content-Type", "application/json"));

        // Act: Call the Feign client method
        Product product = productsServiceClient.product("ABC", 25);

        // Retrieve the request sent to the mock server
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/api/products/25", recordedRequest.getPath());
        //Assertions.assertNull(recordedRequest.getBody());
        Headers headers = recordedRequest.getHeaders();
        assertNotNull(headers.get("Authorization"));
        assertEquals("application/json", headers.get("Content-Type"));
        assertEquals("1234-abcde", headers.get("X-API-KEY"));
        assertEquals("5432", headers.get("X-SPAN-Y"));
        assertEquals("ABC", headers.get("XHACK"));
        // Assert: Verify the response
        assertEquals(25, product.id());
        assertEquals("John Doe", product.name());
        assertEquals("Some description", product.description());
        // Assert: Verify the auditor
        verify(auditor, Mockito.times(1)).audit(auditRequestLogCaptor.capture(), auditResponseLogCaptor.capture());
        AuditRequestLog auditRequestLogValue = auditRequestLogCaptor.getValue();
        AuditResponseLog auditResponseLogValue = auditResponseLogCaptor.getValue();
        assertNotNull(auditRequestLogValue);
        assertNotNull(auditResponseLogValue);
        assertEquals("http://%s:%d/api/products/25".formatted(mockWebServer.getHostName(), mockWebServer.getPort()), auditRequestLogValue.url());
        assertEquals("GET", auditRequestLogValue.httpMethod());
    }
}