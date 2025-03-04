package io.github.jlmc.pizzacondo.inventory.service.adapter.inbound.messaging;

import io.github.jlmc.pizzacondo.common.messages.OrderPlacedEvent;
import io.github.jlmc.pizzacondo.common.messages.OrderValidatedEvent;
import io.github.jlmc.pizzacondo.inventory.service.application.port.input.ValidateOrderUseCase;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class OrderPlacedEventConsumer {

    private final ValidateOrderUseCase validateOrderUseCase;

    public Message<OrderValidatedEvent> consume(OrderPlacedEvent orderPlacedEvent) {

        log.info("consuming order placed event {}", orderPlacedEvent);

        boolean canBeSatisfied =
                validateOrderUseCase.canBeSatisfied(
                        new ValidateOrderUseCase.ValidateOrderCommand(
                            orderPlacedEvent.orderId(), orderPlacedEvent.size(), orderPlacedEvent.toppings()
                ));

        var payload =  new OrderValidatedEvent(orderPlacedEvent.orderId(), canBeSatisfied);

        log.info("replaying order placed event with an OrderValidatedEvent: {}", payload);

        return MessageBuilder
                .withPayload(payload)
                .setHeader(KafkaHeaders.KEY, payload.orderId())
                .build();
    }
}
