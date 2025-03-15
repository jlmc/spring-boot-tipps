package io.github.jlmc.pizzacondo.processing.service.adapter.inbound.messaging;

import io.github.jlmc.pizzacondo.common.messages.OrderAcceptedEvent;
import io.github.jlmc.pizzacondo.processing.service.application.services.PreparingService;
import io.github.jlmc.pizzacondo.processing.service.domain.model.Order;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor

@Component
public class OrderAcceptedEventConsumer {

    private final PreparingService preparingService;

    public void handler(OrderAcceptedEvent event) {
        log.info("OrderAcceptedEvent received: {}", event);
        Order order =
                Order.builder()
                        .id(event.orderId())
                        .placedAt(event.placedAt())
                        .customerId(event.customerId())
                        .size(event.size())
                        .toppings(event.toppings())
                        .build();

        preparingService.prepare(order);
    }
}
