package io.github.jlmc.demo.kafka.interfaces.events;

import io.github.jlmc.demo.kafka.shareddomain.events.BillingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class BillingConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(BillingConsumer.class);

    @KafkaListener(
            topics = "BILLING_TOPIC",
            groupId = "group_id",
            containerFactory = "billingListener"
    )
    public void billing(@Payload BillingEvent billingEvent, @Headers MessageHeaders headers) {
        LOGGER.info(">>> BillingConsumer: <{}> -> <{}>", headers, billingEvent);
    }

}
