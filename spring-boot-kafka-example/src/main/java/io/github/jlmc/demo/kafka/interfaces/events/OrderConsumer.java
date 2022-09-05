package io.github.jlmc.demo.kafka.interfaces.events;

import io.github.jlmc.demo.kafka.shareddomain.events.BillingEvent;
import io.github.jlmc.demo.kafka.shareddomain.events.OrderBookedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderConsumer.class);

    @Autowired
    KafkaTemplate<String, BillingEvent> billingKafkaTemplate;

    @KafkaListener(
            topics = "ORDERS_1",
            groupId = "group_id",
            containerFactory = "orderFactory"
    )
    public void orderListener(@Payload OrderBookedEvent event, @Headers MessageHeaders headers) {

        LOGGER.info("===> OrderConsumer Received <{}> --> <{}>", headers, event);

        BillingEvent billingEvent = new BillingEvent();
        billingEvent.setItemName(event.getItem() + " -> " + event.getId());

        this.billingKafkaTemplate.send("BILLING_TOPIC", event.getId(), billingEvent);
    }

}
