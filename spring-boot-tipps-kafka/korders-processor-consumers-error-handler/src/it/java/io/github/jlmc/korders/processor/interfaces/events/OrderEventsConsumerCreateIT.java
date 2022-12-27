package io.github.jlmc.korders.processor.interfaces.events;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jlmc.korders.ResultCaptor;
import io.github.jlmc.korders.processor.application.commandservices.RegisterNewOrderCommandService;
import io.github.jlmc.korders.processor.domain.model.aggregates.Order;
import io.github.jlmc.korders.processor.domain.model.commands.RegisterNewOrderCommand;
import io.github.jlmc.korders.processor.shareddomain.events.OrderEvent;
import jakarta.persistence.EntityManager;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
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
@AutoConfigureTestEntityManager
@Transactional
public class OrderEventsConsumerCreateIT {

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
    TestEntityManager testEntityManager;

    @BeforeEach
    void setUp() {
        startupListenerContainers(endpointRegistry, embeddedKafkaBroker);
    }

    @Test
    public void publishNewOrderEvent() throws Exception {
        ResultCaptor<Optional<Order>> orderResultCaptor = new ResultCaptor<>();
        doAnswer(orderResultCaptor).when(registerNewOrderCommandServiceSpy).execute(any());

        // given
        String orderId = "1234";
        OrderEvent orderEvent =
                OrderEvent.builder()
                          .orderId(orderId)
                          .type(OrderEvent.Type.CREATED)
                          .instant(LocalDate.parse("2022-12-15").atTime(LocalTime.parse("21:58:12")).atZone(ZoneId.of("UTC")).toInstant())
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

        List<Order> orders =
                em().createQuery("select o from Order o left join FETCH o.items where o.id = :orderId", Order.class)
                    // https://docs.jboss.org/hibernate/orm/6.0/migration-guide/migration-guide.html#query-sqm-distinct
                    //.setHint(HibernateHints.HINT_PASS_DISTINCT_THROUGH, false )
                    //.setHint("hibernate.query.passDistinctThrough", false)
                    /*
                     * Starting with Hibernate ORM 6 it is no longer necessary to use distinct in JPQL and HQL to filter out the same parent entity references when join fetching a child collection. The returning duplicates of entities are now always filtered by Hibernate.
                     * Which means that for instance it is no longer necessary to set QueryHints#HINT_PASS_DISTINCT_THROUGH to false in order to skip the entity duplicates without producing a distinct in the SQL query.
                     * From Hibernate ORM 6, distinct is always passed to the SQL query and the flag QueryHints#HINT_PASS_DISTINCT_THROUGH has been removed.
                     */
                    .setParameter("orderId", orderId)
                    .getResultList();

        assertNotNull(orders);
        assertEquals(1, orders.size());
        assertEquals(orderEvent.getItems().size(), orders.get(0).getItems().size());
        Order returnedOrder = orderResultCaptor.getResult().orElse(null);
        assertEquals(orders.get(0), returnedOrder);
    }

    private EntityManager em() {
        return testEntityManager.getEntityManager();
    }

}
