package io.github.jlmc.korders.processor.interfaces.events;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @see @KafkaListener
 * @see @KafkaHandler
 * @see @KafkaListeners
 * @see @EnableKafkaRetryTopic
 */
@Component
public class OrderEventsConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderEventsConsumer.class);

    @KafkaListener(
            topics = { TopicNames.ORDER_EVENTS_TOPIC }
            //containerFactory = "kafkaListenerContainerFactory" // default value
            //containerFactory = "kafkaListenerContainerFactoryConcurrentConsumerThreads"
    )
    public void onMessage(ConsumerRecord<String, String> consumerRecord) {
        LOGGER.info("On Message < " + Thread.currentThread().getName() + "> " + consumerRecord);
    }
}
