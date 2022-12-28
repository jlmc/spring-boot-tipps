package io.github.jlmc.korders.processor.infrastruture.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.RetryListener;

public class CustomRetryListener implements RetryListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomRetryListener.class);

    @Override
    public void failedDelivery(ConsumerRecord<?, ?> record, Exception ex, int deliveryAttempt) {
        LOGGER.info("Failed record in retry listener delivery Attempt <{}>, exception: {} ", deliveryAttempt, ex.getMessage());
    }
}
