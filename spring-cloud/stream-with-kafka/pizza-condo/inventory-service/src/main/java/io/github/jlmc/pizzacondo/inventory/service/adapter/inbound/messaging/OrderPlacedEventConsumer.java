package io.github.jlmc.pizzacondo.inventory.service.adapter.inbound.messaging;

import io.github.jlmc.pizzacondo.common.messages.OrderPlacedEvent;
import io.github.jlmc.pizzacondo.common.messages.OrderValidatedEvent;
import io.github.jlmc.pizzacondo.inventory.service.application.port.input.ValidateOrderUseCase;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class OrderPlacedEventConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(OrderPlacedEventConsumer.class);

    private final ValidateOrderUseCase validateOrderUseCase;

    public Message<OrderValidatedEvent> consume(OrderPlacedEvent orderPlacedEvent) {

        LOG.info("consuming order placed event {}", orderPlacedEvent);

        var canBeSatisfied =
                validateOrderUseCase.canBeSatisfied(new ValidateOrderUseCase.ValidateOrderCommand(
                        orderPlacedEvent.orderId(), orderPlacedEvent.size(), orderPlacedEvent.toppings()
                ));

        var payload =  new OrderValidatedEvent(orderPlacedEvent.orderId(), canBeSatisfied);

        return MessageBuilder
                .withPayload(payload)
                .setHeader(KafkaHeaders.KEY, payload.orderId())
                .build();
    }
}
