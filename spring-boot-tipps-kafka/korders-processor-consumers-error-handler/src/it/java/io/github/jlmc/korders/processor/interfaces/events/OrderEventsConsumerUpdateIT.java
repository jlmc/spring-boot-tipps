package io.github.jlmc.korders.processor.interfaces.events;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jlmc.korders.ResultCaptor;
import io.github.jlmc.korders.processor.application.commandservices.RegisterNewOrderCommandService;
import io.github.jlmc.korders.processor.domain.model.aggregates.Order;
import io.github.jlmc.korders.processor.domain.model.commands.RegisterNewOrderCommand;
import io.github.jlmc.korders.processor.domain.model.entities.OrderItem;
import io.github.jlmc.korders.processor.domain.model.valueobjects.Product;
import io.github.jlmc.korders.processor.infrastruture.repositories.OrderRepository;
import io.github.jlmc.korders.processor.shareddomain.events.OrderEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static io.github.jlmc.korders.processor.interfaces.events.Setups.startupListenerContainers;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"orders-events"}, partitions = 3)
@TestPropertySource(properties = {
        "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "retryListener.startup=false"
})
public class OrderEventsConsumerUpdateIT {

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

    @Captor
    ArgumentCaptor<RegisterNewOrderCommand> commandCaptor;

    @Autowired
    OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        startupListenerContainers(endpointRegistry, embeddedKafkaBroker);
    }

    @Test
    public void publishUpdateOrderEvent() throws Exception {
        ResultCaptor<Optional<Order>> orderResultCaptor = new ResultCaptor<>();
        doAnswer(orderResultCaptor).when(registerNewOrderCommandServiceSpy).execute(any());

        // given

        String orderId = "9991";
        Instant utc = LocalDate.parse("2022-12-15").atTime(LocalTime.parse("21:58:12")).atZone(ZoneId.of("UTC")).toInstant();
        orderRepository.save(Order.builder()
                                  .id(orderId)
                                  .orderCreated(utc)
                                  .items(Set.of(
                                          OrderItem.builder().product(Product.of("54","P1")).tax(BigDecimal.ONE).unitPrice(BigDecimal.TEN).qty(1).build(),
                                          OrderItem.builder().product(Product.of("56","P5")).tax(BigDecimal.ONE).unitPrice(BigDecimal.TEN).qty(1).build(),
                                          OrderItem.builder().product(Product.of("55","P4")).tax(BigDecimal.ONE).unitPrice(BigDecimal.TEN).qty(12).build()))
                                  .build());


        OrderEvent orderEvent =
                OrderEvent.builder()
                          .orderId(orderId)
                          .type(OrderEvent.Type.UPDATED)
                          .instant(utc)
                          .items(List.of(
                                  OrderEvent.Item.of("1", 1),
                                  OrderEvent.Item.of("2", 10)))
                          .build();
        String eventJson = objectMapper.writeValueAsString(orderEvent);
        kafkaTemplate.send(TopicNames.ORDER_EVENTS_TOPIC, orderId, eventJson).get();

        // when
        CountDownLatch latch = new CountDownLatch(1);
        latch.await(3L, TimeUnit.SECONDS);


        // then
        verify(orderEventsConsumerSpy, times(1))
                .onMessage(isA(ConsumerRecord.class));


        verify(this.registerNewOrderCommandServiceSpy, times(1))
                .execute(commandCaptor.capture());

        RegisterNewOrderCommand command = commandCaptor.getValue();

        assertNotNull(command);
        assertEquals(orderId, command.orderId());
        assertEquals(orderEvent.getInstant(), command.created());
        assertEquals(orderEvent.getItems().size(), command.items().size());

        Order savedOrder = orderRepository.findOrderByIdFetchItems(orderId).orElse(null);
        assertNotNull(savedOrder);
        assertEquals(orderEvent.getItems().size(), savedOrder.getItems().size());
        Order returnedOrder = orderResultCaptor.getResult().orElse(null);
        assertEquals(savedOrder, returnedOrder);

    }

}
