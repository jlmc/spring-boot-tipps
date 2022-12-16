package io.github.jlmc.korders.processor.interfaces.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jlmc.korders.processor.application.commandservices.RegisterNewOrderCommandService;
import io.github.jlmc.korders.processor.domain.model.commands.RegisterNewOrderCommand;
import io.github.jlmc.korders.processor.shareddomain.events.OrderEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

/**
 * @see @KafkaListener
 * @see @KafkaHandler
 * @see @KafkaListeners
 * @see @EnableKafkaRetryTopic
 */


@Component
public class OrderEventsConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderEventsConsumer.class);

    private final RegisterNewOrderCommandService registerNewOrderCommandService;

    private final ObjectMapper objectMapper;

    public OrderEventsConsumer(
            RegisterNewOrderCommandService registerNewOrderCommandService,
            ObjectMapper objectMapper) {
        this.registerNewOrderCommandService = registerNewOrderCommandService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = {TopicNames.ORDER_EVENTS_TOPIC}
            //containerFactory = "kafkaListenerContainerFactory" // default value
            //containerFactory = "kafkaListenerContainerFactoryConcurrentConsumerThreads"
    )
    public void onMessage(ConsumerRecord<String, String> consumerRecord) {
        LOGGER.info("On Message < " + Thread.currentThread().getName() + "> " + consumerRecord);

        String value = consumerRecord.value();

        OrderEvent orderEvent = readEvent(value);
        String orderId = orderEvent.getOrderId();
        Instant instant = orderEvent.getInstant();
        List<Pair<String, Integer>> items =
                orderEvent.getItems()
                          .stream()
                          .map(p -> Pair.of(p.getProductId(), p.getQty()))
                          .toList();

        registerNewOrderCommandService.execute(new RegisterNewOrderCommand(orderId, instant, items));
    }


    private OrderEvent readEvent(String value) {
        try {
            return objectMapper.readValue(value, OrderEvent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
