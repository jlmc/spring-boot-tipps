package io.github.jlmc.pizzacondo.om.service.adapter.inbound.messaging;

import io.github.jlmc.pizzacondo.common.messages.OrderValidatedEvent;
import io.github.jlmc.pizzacondo.om.service.application.port.input.CancelOrderUseCase;
import io.github.jlmc.pizzacondo.om.service.application.port.input.DispatchOrderUseCase;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class OrderValidatedConsumer {

    final CancelOrderUseCase cancelOrderUseCase;
    final DispatchOrderUseCase dispatchOrderUseCase;

    public void handler(OrderValidatedEvent event) {

        log.info("Received order validated event orderId: {} canBeSatisfied {}", event.orderId(), event.canBeSatisfied());

        if (event.canBeSatisfied()) {
            dispatchOrderUseCase.dispatchOrder(event.orderId());
        } else {
            cancelOrderUseCase.canselOrder(event.orderId());
        }

    }
}
