package io.github.jlmc.korders.processor.interfaces.events;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jlmc.korders.ResultCaptor;
import io.github.jlmc.korders.processor.application.commandservices.RegisterNewOrderCommandService;
import io.github.jlmc.korders.processor.domain.model.aggregates.Order;
import io.github.jlmc.korders.processor.domain.model.commands.RegisterNewOrderCommand;
import io.github.jlmc.korders.processor.infrastruture.repositories.OrderRepository;
import io.github.jlmc.korders.processor.shareddomain.events.OrderEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static io.github.jlmc.korders.processor.interfaces.events.Setups.startupListenerContainers;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;


@DisplayName("kafka consumer, Retry SpecificExceptions using Custom RetryPolicy IT")
@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"orders-events"}, partitions = 3)
@TestPropertySource(properties = {
        "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "retryListener.startup=false"
})
public class RetrySpecificEceptionsUsingCustomRetryPolicyIT {

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    /**
     * The KafkaListenerEndpointRegistry have access to all the kafkaListeners
     */
    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    KafkaListenerEndpointRegistry endpointRegistry;
    @Autowired
    ObjectMapper objectMapper;

    // Spy bean, it is a bean that give us access to the real bean
    @SpyBean
    OrderEventsConsumer orderEventsConsumerSpy;
    @SpyBean
    RegisterNewOrderCommandService registerNewOrderCommandServiceSpy;

    @Autowired
    OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        startupListenerContainers(endpointRegistry, embeddedKafkaBroker);
    }

    @Test
    @DisplayName("when one exception marked as not Not Retryable, it should not retry process the record")
    public void publishNewOrderEvent() throws Exception {
        ResultCaptor<Optional<Order>> orderResultCaptor = new ResultCaptor<>();
        doAnswer(orderResultCaptor).when(registerNewOrderCommandServiceSpy).execute(any());

        // given
        String orderId = "invalid-order-1234";
        OrderEvent orderEvent =
                OrderEvent.builder()
                          .orderId(orderId)
                          .type(OrderEvent.Type.CREATED)
                          .instant(LocalDate.parse("2022-12-15").atTime(LocalTime.parse("21:58:12")).atZone(ZoneId.of("UTC")).toInstant())
                          .items(List.of(OrderEvent.Item.of("9999_9999", 1)))
                          .build();
        String eventJson = objectMapper.writeValueAsString(orderEvent);
        kafkaTemplate.send(TopicNames.ORDER_EVENTS_TOPIC, orderId, eventJson).get();

        // when
        CountDownLatch latch = new CountDownLatch(1);
        latch.await(3L, TimeUnit.SECONDS);

        //then
        verify(orderEventsConsumerSpy, Mockito.times(1)).onMessage(isA(ConsumerRecord.class));
        verify(registerNewOrderCommandServiceSpy, times(1)).execute(isA(RegisterNewOrderCommand.class));

        assertFalse(orderRepository.existsById(orderId));
    }
}
