package io.github.jlmc.springbootkafkaexamplerx.interfaces.events;

import io.github.jlmc.springbootkafkaexamplerx.sharewddomain.BillingEvent;
import io.github.jlmc.springbootkafkaexamplerx.sharewddomain.OrderBookedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class OrderConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderConsumer.class);

    @Autowired
    KafkaTemplate<String, BillingEvent> billingEvents;

    @KafkaListener(
            topics = "ORDERS_1",
            groupId = "group_id",
            containerFactory = "orderFactory"
    )
    public void orderListener(@Payload OrderBookedEvent event, @Headers MessageHeaders headers) {

        LOGGER.info("===> OrderConsumer Received <{}> --> <{}>", headers, event);

        BillingEvent billingEvent = new BillingEvent();
        billingEvent.setItemName(event.getItem() + " -> " + event.getId());

        this.billingEvents.send("BILLING_TOPIC", event.getId(), billingEvent)
                .thenAccept(new Consumer<SendResult<String, BillingEvent>>() {
                    @Override
                    public void accept(SendResult<String, BillingEvent> result) {
                        LOGGER.info("Event Success Sent: [{}]", result.getRecordMetadata());
                    }
                }).exceptionally(new Function<Throwable, Void>() {
                    @Override
                    public Void apply(Throwable throwable) {
                        LOGGER.error("Event Not Sent {}", throwable.getMessage(), throwable);
                        return null;
                    }
                });
    }
}
