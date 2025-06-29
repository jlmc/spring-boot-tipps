package io.github.jlmc.orders.interfaces.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jlmc.orders.ClassPathResources;
import io.github.jlmc.orders.interfaces.rest.domain.OrderRepresentation;
import io.github.jlmc.orders.interfaces.rest.domain.OrderRequest;
import io.github.jlmc.orders.shareddomain.events.OrderEvent;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static io.github.jlmc.orders.TopicNames.ORDER_EVENTS_TOPIC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext // Ensures context is cleaned after test
@EmbeddedKafka(
        partitions = 1,
        topics = {ORDER_EVENTS_TOPIC},
        brokerProperties = {
                "listeners=PLAINTEXT://localhost:9092",
                "port=9092"
        })
/*
@TestPropertySource(properties = {
        "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"})
 */
class OrdersControllerIT {

    @Autowired
    TestRestTemplate restTemplate;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    ServerProperties serverProperties;

    @LocalServerPort
    int randomServerPort;

    // Inject which port we were assigned
    @Value("${local.server.port}")
    int port;

    @Value("${spring.embedded.kafka.brokers}")
    String brokers;

    @Autowired
    private ObjectMapper objectMapper;


    @DynamicPropertySource
    static void registerKafka(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", () -> "localhost:9092");
    }


    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    @DisplayName("""
            when an order request is received with a valid format,
            it should create a order and publish a order create event to the orders-events kafka topic
            """)
    void postOrderSuccessful() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", MediaType.APPLICATION_JSON.toString());

        byte[] bytes = ClassPathResources.fromFile("request-payloads/create-order-request-payload.json");
        OrderRequest payload = objectMapper.readValue(new String(bytes), OrderRequest.class);

        HttpEntity<OrderRequest> request = new HttpEntity<>(payload, headers);
        ResponseEntity<OrderRepresentation> responseEntity =
                restTemplate.exchange(
                        "/v1/orders",
                        HttpMethod.POST, request, OrderRepresentation.class);


        OrderRepresentation orderResponseRepresentation = responseEntity.getBody();
        assertNotNull(orderResponseRepresentation);
        assertNotNull(orderResponseRepresentation.getId());
        assertNotNull(orderResponseRepresentation.getCreated());
        assertNotNull(orderResponseRepresentation.getItems());
        assertEquals(payload.getItems().size(), orderResponseRepresentation.getItems().size());
        String headerLocation = Optional.ofNullable(responseEntity.getHeaders().get(HttpHeaders.LOCATION))
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .orElse(null);
        assertNotNull(headerLocation);
        assertEquals("http://localhost:" + randomServerPort + "/v1/orders/" + orderResponseRepresentation.getId(), headerLocation);

        // Set up test consumer
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafkaBroker);
        Consumer<String, String> consumer =
                new DefaultKafkaConsumerFactory<>(
                        consumerProps,
                        new StringDeserializer(),
                        new StringDeserializer()
                ).createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, ORDER_EVENTS_TOPIC);

        ConsumerRecord<String, String> consumerRecord = KafkaTestUtils.getSingleRecord(consumer, ORDER_EVENTS_TOPIC);
        String value = consumerRecord.value();
        String key = consumerRecord.key();

        OrderEvent event = objectMapper.readValue(value, OrderEvent.class);
        assertNotNull(event);
        assertNotNull(key);
        assertEquals(orderResponseRepresentation.getId(), key);
        assertEquals(OrderEvent.Type.CREATED, event.getType());
        assertEquals(orderResponseRepresentation.getId(), event.getOrderId());
    }

}
