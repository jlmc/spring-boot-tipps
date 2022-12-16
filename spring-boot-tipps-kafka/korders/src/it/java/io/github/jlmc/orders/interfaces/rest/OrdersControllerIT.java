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
import org.junit.jupiter.api.*;
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
import org.springframework.test.context.TestPropertySource;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static io.github.jlmc.orders.TopicNames.ORDER_EVENTS_TOPIC;

/**
 * @see org.springframework.kafka.test.EmbeddedKafkaBroke
 * @see @LocalManagementPort
 */
@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"orders-events"}, partitions = 3)
@TestPropertySource(properties = {
        "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"
})
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

    @Autowired
    private ObjectMapper objectMapper;

    private Consumer<String, String> consumer;

    @BeforeEach
    void setUp() {
        Map<String, Object> configs =
                new HashMap<>(KafkaTestUtils.consumerProps("group1", "true", embeddedKafkaBroker));
        consumer =
                new DefaultKafkaConsumerFactory<>(
                        configs,
                        new StringDeserializer(),
                        new StringDeserializer()
                ).createConsumer();
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
    }

    @AfterEach
    void tearDown() {
        consumer.close();
    }

    @Test
    @Timeout(5)
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
        Assertions.assertNotNull(orderResponseRepresentation);
        Assertions.assertNotNull(orderResponseRepresentation.getId());
        Assertions.assertNotNull(orderResponseRepresentation.getCreated());
        Assertions.assertNotNull(orderResponseRepresentation.getItems());
        Assertions.assertEquals(payload.getItems().size(), orderResponseRepresentation.getItems().size());
        String headerLocation = Optional.ofNullable(responseEntity.getHeaders().get(HttpHeaders.LOCATION))
                                        .stream()
                                        .flatMap(Collection::stream)
                                        .findFirst()
                                        .orElse(null);
        Assertions.assertNotNull(headerLocation);
        Assertions.assertEquals("http://localhost:" + randomServerPort + "/v1/orders/" + orderResponseRepresentation.getId(), headerLocation);

        ConsumerRecord<String, String> consumerRecord = KafkaTestUtils.getSingleRecord(consumer, ORDER_EVENTS_TOPIC);
        String value = consumerRecord.value();
        String key = consumerRecord.key();

        OrderEvent event = objectMapper.readValue(value, OrderEvent.class);
        Assertions.assertNotNull(event);
        Assertions.assertNotNull(key);
        Assertions.assertEquals(orderResponseRepresentation.getId(), key);
        Assertions.assertEquals(OrderEvent.Type.CREATED, event.getType());
        Assertions.assertEquals(orderResponseRepresentation.getId(), event.getOrderId());
    }

}
