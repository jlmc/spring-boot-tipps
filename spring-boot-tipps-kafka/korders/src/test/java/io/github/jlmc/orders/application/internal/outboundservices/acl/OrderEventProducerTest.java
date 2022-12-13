package io.github.jlmc.orders.application.internal.outboundservices.acl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jlmc.orders.domain.model.aggregates.Order;
import io.github.jlmc.orders.domain.model.entities.Product;
import io.github.jlmc.orders.domain.model.valueobjects.OrderItem;
import io.github.jlmc.orders.shareddomain.events.OrderEvent;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static io.github.jlmc.orders.TopicNames.ORDER_EVENTS_TOPIC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderEventProducerTest {

    @Mock
    KafkaTemplate<String, String> kafkaTemplate;

    @Spy
    ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @InjectMocks
    OrderEventProducer orderEventProducer;

    @Captor
    ArgumentCaptor<String> keyCaptor;
    @Captor
    ArgumentCaptor<String> valueCaptor;

    Instant instant =
            LocalDate.parse("2022-12-13")
                     .atTime(LocalTime.parse("19:32:00"))
                     .atZone(ZoneId.of("UTC"))
                     .toInstant();

    @Test
    void sendCreateOrderEvent_approach_1_success() throws JsonProcessingException {
        Order order = Order.builder()
                           .id("1")
                           .created(instant)
                           .orderItems(List.of(
                                           OrderItem.of(Product.of("2", "iphone-xs"), 2),
                                           OrderItem.of(Product.of("3", "macbook pro"), 1)
                                   )
                           )
                           .build();


        when(kafkaTemplate.sendDefault(Mockito.eq(order.getId()), isA(String.class)))
                .thenAnswer((Answer<CompletableFuture<SendResult<String, String>>>) invocationOnMock -> {
                            String key = invocationOnMock.getArgument(0);
                            String value = invocationOnMock.getArgument(1);
                            ProducerRecord<String, String> producerRecord = new ProducerRecord<>(ORDER_EVENTS_TOPIC, null, key, value);
                            RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition(ORDER_EVENTS_TOPIC, 1), 0L, 1, instant.toEpochMilli(), 100, 100);

                            return CompletableFuture.supplyAsync(() -> new SendResult<>(producerRecord, recordMetadata));
                        }
                );

        orderEventProducer.sendCreateOrderEvent(order);

        verify(kafkaTemplate).sendDefault(keyCaptor.capture(), valueCaptor.capture());
        OrderEvent event = objectMapper.readValue(valueCaptor.getValue(), OrderEvent.class);
        assertNotNull(event);
        assertEquals(order.getId(), event.getOrderId());
        assertEquals(keyCaptor.getValue(), event.getOrderId());
    }

    @Test
    void sendCreateOrderEvent_approach_1_failure() throws JsonProcessingException {
        Order order = Order.builder()
                           .id("1")
                           .created(instant)
                           .orderItems(List.of(
                                           OrderItem.of(Product.of("2", "iphone-xs"), 2),
                                           OrderItem.of(Product.of("3", "macbook pro"), 1)
                                   )
                           )
                           .build();

        when(kafkaTemplate.sendDefault(Mockito.eq(order.getId()), isA(String.class)))
                .thenAnswer((Answer<CompletableFuture<SendResult<String, String>>>) invocationOnMock ->
                        CompletableFuture.failedFuture(new RuntimeException("Exception Calling Kafka"))
                );

        assertThrows(RuntimeException.class, () -> orderEventProducer.sendCreateOrderEvent(order));

        verify(kafkaTemplate).sendDefault(keyCaptor.capture(), valueCaptor.capture());
        OrderEvent event = objectMapper.readValue(valueCaptor.getValue(), OrderEvent.class);
        assertNotNull(event);
        assertEquals(order.getId(), event.getOrderId());
        assertEquals(order.getCreated(), event.getInstant());
        assertEquals(keyCaptor.getValue(), event.getOrderId());
    }
}
