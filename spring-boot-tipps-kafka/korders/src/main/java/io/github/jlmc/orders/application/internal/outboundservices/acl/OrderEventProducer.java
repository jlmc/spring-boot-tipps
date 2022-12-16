package io.github.jlmc.orders.application.internal.outboundservices.acl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jlmc.orders.domain.model.aggregates.Order;
import io.github.jlmc.orders.shareddomain.events.OrderEvent;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
public class OrderEventProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderEventProducer.class);
    private static final String ORDERS_EVENTS_TOPIC = "orders-events";

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;

    private static ProducerRecord<String, String> toProducerRecord(String key, String value, String topic) {

        List<Header> headers = List.of(
                new RecordHeader("event-source", "orders-app".getBytes(StandardCharsets.UTF_8))
        );

        return new ProducerRecord<>(topic, null, key, value, headers);
    }

    public void sendUpdateOrderEvent(Order updatedOrder) {
        var value = toOrderEvent(updatedOrder, OrderEvent.Type.UPDATED);
        var key = updatedOrder.getId();
        sendEventToDefaultTopicSync(key, value);
    }

    public void sendCreateOrderEvent(Order newOrder) {
        var value = toOrderEvent(newOrder, OrderEvent.Type.CREATED);
        String key = newOrder.getId();
        sendEventToDefaultTopicSync(key, value);
    }

    private void sendEventToDefaultTopicSync(String key, String value) {
        CompletableFuture<SendResult<String, String>> listenableFuture =
                kafkaTemplate.sendDefault(key, value)
                             .orTimeout(1L, TimeUnit.SECONDS);

        SendResult<String, String> sendResult;
        try {
            sendResult = listenableFuture.get();
            onComplete(key, value, sendResult);
        } catch (InterruptedException | ExecutionException e) {
            onError(key, value, e);
            throw new RuntimeException(e);
        }
    }

    public void sendCreateOrderEventAsync(Order newOrder) {
        var value = toOrderEvent(newOrder, OrderEvent.Type.CREATED);
        String key = newOrder.getId();
        CompletableFuture<SendResult<String, String>> listenableFuture =
                kafkaTemplate.sendDefault(key, value);


        listenableFuture.thenAcceptAsync(s -> onComplete(key, value, s))
                        .exceptionallyAsync((Throwable t) -> onError(key, value, t));

        /*
        listenableFuture.handleAsync((SendResult<String, String> v, Throwable t) -> {
            if (t != null) {
                this.onError(key, value, t);
            } else {
                this.onComplete(key, value, v);
            }

            return v;
        });
         */
    }

    public void sendCreateOrderEventAsync2(Order newOrder) {
        var value = toOrderEvent(newOrder, OrderEvent.Type.CREATED);
        String key = newOrder.getId();

        ProducerRecord<String, String> producerRecord = toProducerRecord(key, value, ORDERS_EVENTS_TOPIC);

        //kafkaTemplate.send(topic, key, value);
        kafkaTemplate.send(producerRecord)
                     .orTimeout(1L, TimeUnit.SECONDS)
                     .thenAcceptAsync(s -> onComplete(key, value, s))
                     .exceptionallyAsync(t -> onError(key, value, t));
    }

    private Void onError(String key, String value, Throwable throwable) {
        LOGGER.error("Could not push the event to kafka topic <{}> <{}> <{}>", key, value, throwable.getMessage(), throwable);
        return null;
    }

    private void onComplete(String key, String value, SendResult<String, String> v) {
        LOGGER.info("Successful push the event to kafka topic <{}> <{}> <{}>", key, value, v.getRecordMetadata().partition());
    }

    private String toJson(OrderEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String toOrderEvent(Order newOrder, OrderEvent.Type type) {
        OrderEvent event = OrderEvent.builder()
                                     .orderId(newOrder.getId())
                                     .instant(newOrder.getCreated())
                                     .type(type)
                                     .items(newOrder.getOrderItems()
                                                    .stream()
                                                    .map(it -> OrderEvent.Item.of(it.getProduct().getId(), it.getQty()))
                                                    .toList())
                                     .build();


        return toJson(event);
    }


}
