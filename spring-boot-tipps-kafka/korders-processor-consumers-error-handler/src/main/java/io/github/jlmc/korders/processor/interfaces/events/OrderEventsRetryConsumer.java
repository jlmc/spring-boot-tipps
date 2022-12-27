package io.github.jlmc.korders.processor.interfaces.events;

import io.github.jlmc.korders.processor.application.commandservices.RegisterNewOrderCommandService;
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
public class OrderEventsRetryConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderEventsRetryConsumer.class);

    private final RegisterNewOrderCommandService registerNewOrderCommandService;
    private final OrderEventCommandAssembler orderEventCommandAssembler;

    public OrderEventsRetryConsumer(
            RegisterNewOrderCommandService registerNewOrderCommandService,
            OrderEventCommandAssembler orderEventCommandAssembler) {
        this.registerNewOrderCommandService = registerNewOrderCommandService;
        this.orderEventCommandAssembler = orderEventCommandAssembler;
    }

    @KafkaListener(topics = {"${x.topics.retry}"},
            autoStartup = "${retryListener.startup:true}",
            groupId = "retry-listener-group"
    )
    public void onMessage(ConsumerRecord<String, String> consumerRecord) {
        LOGGER.info("ConsumerRecord in Retry Consumer : {} ", consumerRecord);
        // Just to make sure that all the
        consumerRecord.headers()
                      .forEach(header -> {
                          LOGGER.info("Key : {} , value : {}", header.key(), new String(header.value()));
                      });

        registerNewOrderCommandService.execute(orderEventCommandAssembler.toCommand(consumerRecord));
    }
}
