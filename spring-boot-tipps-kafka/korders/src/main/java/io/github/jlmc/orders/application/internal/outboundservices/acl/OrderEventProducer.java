package io.github.jlmc.orders.application.internal.outboundservices.acl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jlmc.orders.domain.model.aggregates.Order;
import io.github.jlmc.orders.shareddomain.events.OrderEvent;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
public class OrderEventProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderEventProducer.class);

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;

    private static OrderEvent toOrderEvent(Order newOrder) {
        OrderEvent event = OrderEvent.builder()
                                     .orderId(newOrder.getId())
                                     .instant(newOrder.getCreated())
                                     .type(OrderEvent.Type.CREATED)
                                     .items(newOrder.getOrderItems()
                                                    .stream()
                                                    .map(it -> OrderEvent.Item.of(it.getProduct().getId(), it.getQty()))
                                                    .toList())
                                     .build();
        return event;
    }

    public void sendCreateOrderEvent(Order newOrder) {
        OrderEvent event = toOrderEvent(newOrder);

        String key = newOrder.getId();
        String value = toJson(event);
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
        OrderEvent event = toOrderEvent(newOrder);
        String key = newOrder.getId();
        String value = toJson(event);
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
        OrderEvent event = toOrderEvent(newOrder);
        String key = newOrder.getId();
        String value = toJson(event);

        String topic = "orders-events";
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, null, key, value, null);

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
}
