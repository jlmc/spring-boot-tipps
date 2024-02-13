package io.github.jlmc.docsprocessing.service.infrastructure.bpm.messaging.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jlmc.docsprocessing.service.commons.gateway.response.StartProcessDto;
import junit.framework.AssertionFailedError;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * <h2>Testing Spring Bridge Based Publisher</h2>
 * <p>
 * To implement this tests we have add the dependencies:
 * - org.springframework.cloud:spring-cloud-stream-test-binder
 * - org.springframework.boot:spring-boot-testcontainers
 * - org.testcontainers:junit-jupiter
 * - org.testcontainers:rabbitmq
 *
 * <p>
 * Once included there are two ways to code the test. One way is to import the TestChannelBinderConfiguration:
 *
 */
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import(TestChannelBinderConfiguration.class) // This is important, because it enables the OutputDestination resolution
class DefaultBpmProducerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultBpmProducerTest.class);

    @Container
    private static final RabbitMQContainer rabbitMQContainer =
            new RabbitMQContainer("rabbitmq:3.8.16-management");
    public static final String EX_BPM_START_PROCESS = "bpm.start.process";

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry) {
        LOGGER.info("Overriding the default properties with test container!");
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getAmqpPort);
        registry.add("spring.datasource.host", rabbitMQContainer::getHost);
        registry.add("spring.datasource.username", rabbitMQContainer::getAdminUsername);
        registry.add("spring.datasource.password", rabbitMQContainer::getAdminPassword);
    }

    @Autowired
    DefaultBpmProducer sut;

    @Autowired
    OutputDestination outputDestination;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ApplicationContext context;

    @Test
    void when_execute_startProcess_it_publishes_the_message_to_the_expected_destination() {
        final String processName = "test-1";

        sut.startProcess(processName);
        Message<byte[]> receivedMessage = outputDestination.receive(3_000, EX_BPM_START_PROCESS);

        assertNotNull(receivedMessage);
        MessageHeaders headers = receivedMessage.getHeaders();
        byte[] payload = receivedMessage.getPayload();

        assertNotNull(headers);
        assertNotNull(headers.get("id"));
        assertNotNull(headers.get("contentType"));
        assertNotNull(headers.get("target-protocol"));
        assertNotNull(headers.get("timestamp"));
        assertEquals("application/json", headers.get("contentType"));
        assertEquals("kafka", headers.get("target-protocol"));

        assertNotNull(payload);
        LOGGER.info("The payload as string: {}", new String(payload, StandardCharsets.UTF_8));

        StartProcessDto startProcessDto = readValueAs(payload, StartProcessDto.class);
        assertEquals(processName, startProcessDto.processName());
    }

    @Test
    void when_execute_correlateMessage_it_publishes_the_message_to_the_expected_destination() {
        DefaultBpmProducer.CorrelateMessageDto input = new DefaultBpmProducer.CorrelateMessageDto();

        sut.correlateMessage(input);

        String destination = context.getEnvironment().getProperty("spring.cloud.stream.bindings.correlateMessage.destination");
        String appName = context.getEnvironment().getProperty("spring.application.name");
        String routingExpression = context.getEnvironment().getProperty("config.messaging.routingExpression");

        Message<byte[]> receivedMessage = outputDestination.receive(3_000, destination);

        assertNotNull(receivedMessage);
        MessageHeaders headers = receivedMessage.getHeaders();
        byte[] payload = receivedMessage.getPayload();

        assertNotNull(headers);
        assertNotNull(headers.get("id"));
        assertNotNull(headers.get("contentType"));
        assertNotNull(headers.get("target-protocol"));
        assertNotNull(headers.get("timestamp"));
        assertNotNull(routingExpression);
        assertNotNull(headers.get(routingExpression));
        assertEquals("application/json", headers.get("contentType"));
        assertEquals("kafka", headers.get("target-protocol"));
        assertEquals(appName, headers.get(routingExpression));

        assertNotNull(payload);
        LOGGER.info("The payload as string: {}", new String(payload, StandardCharsets.UTF_8));

        DefaultBpmProducer.CorrelateMessageDto received = readValueAs(payload, DefaultBpmProducer.CorrelateMessageDto.class);
        assertEquals(input, received);
    }

    private <T> T readValueAs(byte[] s, Class<T> clazz) {
        try {
            return objectMapper.readValue(s, clazz);
        } catch (IOException e) {
            throw new AssertionFailedError(e.toString());
        }
    }
}
