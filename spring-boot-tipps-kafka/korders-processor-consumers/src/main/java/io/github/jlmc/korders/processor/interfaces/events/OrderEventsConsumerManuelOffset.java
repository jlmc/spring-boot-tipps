package io.github.jlmc.korders.processor.interfaces.events;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;

/**
 * This class show how we can implement committing offset manually. exists only for didactic proposes.
 * Alternative to the default kafkaListenerContainerFactory.
 *  This configurations be enabled only when we want to use
 *  commit the offset MANUAL
 *
 * @see org.springframework.boot.autoconfigure.kafka.KafkaAnnotationDrivenConfiguration#kafkaListenerContainerFactory
 * @see org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration
 */
@ConditionalOnProperty(
        prefix = "xxy",
        name = "commit.offset",
        havingValue = "MANUAL",
        matchIfMissing = false
)
//@org.springframework.stereotype.Component
public class OrderEventsConsumerManuelOffset implements AcknowledgingMessageListener<String, String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderEventsConsumerManuelOffset.class);

    @Override
    @KafkaListener(
            topics = { TopicNames.ORDER_EVENTS_TOPIC },
            containerFactory = "kafkaListenerContainerFactoryManualAckMode",
            groupId = "test-1"
    )
    public void onMessage(ConsumerRecord<String, String> data, Acknowledgment acknowledgment) {
        LOGGER.info("On Message Manual: " + data);

        acknowledgment.acknowledge();
    }
}
