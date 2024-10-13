package io.github.jlmc.poc.configurations.actuator.health;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Component
@ConditionalOnEnabledHealthIndicator("external-service-health")
public class ExternalServiceHealthIndicator implements HealthIndicator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalServiceHealthIndicator.class);

    // Create an ExecutorService
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(2);

    @Value("${spring.cloud.openfeign.client.config.order-id-generator-service-client.url}")
    String productsServiceUrl;

    @Value("${spring.application.name}")
    String applicationName;

    @Autowired
    Clock clock;

    @Autowired
    ObjectMapper objectMapper;

    HttpClient client;

    @PostConstruct
    public void init() {
        client = HttpClient.newBuilder()
                .executor(EXECUTOR)
                .connectTimeout(Duration.ofSeconds(10))
                .priority(1)
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .authenticator(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("x-u1", "x-p1".toCharArray());
                    }
                })
                .build();
    }

    @PreDestroy
    public void shutdown() {
        client.close();
        EXECUTOR.shutdown();
    }

    @Override
    public Health health() {

        Result result = connect();
        LOGGER.info("Connected to external service: {}", result);

        return switch (result.status) {
            case UP -> new Health.Builder()
                    .up()
                    .withDetail("External_Service", "Service is Up and Running ✅")
                    .withDetail("External_Service_URL", productsServiceUrl)
                    .build();
            case DOWN -> new Health.Builder().down()
                    .withDetail("External_Service", "Service is Down 🔻")
                    .withDetail("alternative_url", "https://alt-example.com")
                    .build();
            default -> {
                Health.Builder unknown = new Health.Builder().unknown();
                if (result.cause() != null) {
                    unknown.withException(result.cause());
                }
                yield unknown.build();
            }
        };
    }

    private Result connect() {
        try {

            LOGGER.info("Connecting to {}", productsServiceUrl);

            String json = objectMapper.writeValueAsString(new ExternalServiceRequestBody(applicationName, Instant.now(clock)));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(this.productsServiceUrl + "/flip-flop"))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            var response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                   //.thenApply(HttpResponse::body)
                    //.thenAccept(System.out::println)
                    .exceptionally(e -> {
                        System.out.println("Request failed: " + e.getMessage());
                        return null;
                    })
                    .join();

            int statusCode = response.statusCode();

            if (statusCode >= 200 && statusCode < 300) {
                return new Result(ExternalServiceStatus.UP, null);
            }

            if (statusCode == 503) {
                return new Result(ExternalServiceStatus.DOWN, null);
            }

            throw new RuntimeException("Unexpected response code: " + statusCode);

        } catch (Exception e) {
            LOGGER.error("Failed to connect to external service", e);
            return new Result(ExternalServiceStatus.DOWN, e);
        }
    }

    public enum ExternalServiceStatus {
        UP,
        DOWN,
        UNKNOWN,
    }

    record Result(ExternalServiceStatus status, Exception cause) {
    }

    record ExternalServiceRequestBody(String app, Instant instant) {
    }
}