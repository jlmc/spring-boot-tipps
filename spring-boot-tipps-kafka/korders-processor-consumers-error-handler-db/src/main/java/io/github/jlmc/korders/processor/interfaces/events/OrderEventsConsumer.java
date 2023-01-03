package io.github.jlmc.korders.processor.interfaces.events;

import io.github.jlmc.korders.processor.application.commandservices.RegisterNewOrderCommandService;
import io.github.jlmc.korders.processor.domain.model.commands.RegisterNewOrderCommand;
import io.github.jlmc.korders.processor.interfaces.events.transform.OrderEventCommandAssembler;
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

    private final RegisterNewOrderCommandService registerNewOrderCommandService;
    private final OrderEventCommandAssembler orderEventCommandAssembler;

    public OrderEventsConsumer(
            RegisterNewOrderCommandService registerNewOrderCommandService,
            OrderEventCommandAssembler orderEventCommandAssembler) {
        this.registerNewOrderCommandService = registerNewOrderCommandService;
        this.orderEventCommandAssembler = orderEventCommandAssembler;
    }

    @KafkaListener(
            topics = {TopicNames.ORDER_EVENTS_TOPIC},
            groupId = "order-events-listener-group"
            //containerFactory = "kafkaListenerContainerFactory" // default value
            //containerFactory = "kafkaListenerContainerFactoryConcurrentConsumerThreads"
    )
    public void onMessage(ConsumerRecord<String, String> consumerRecord) {
        LOGGER.info("On Message < " + Thread.currentThread().getName() + "> " + consumerRecord);

        RegisterNewOrderCommand command = orderEventCommandAssembler.toCommand(consumerRecord);

        registerNewOrderCommandService.execute(command);
    }
}
