package io.github.jlmc.pizzacondo.om.service.adapter.inbound.messaging;

import io.github.jlmc.pizzacondo.om.service.application.port.input.CancelOrderUseCase;
import io.github.jlmc.pizzacondo.om.service.application.port.input.DispatchOrderUseCase;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class OrderValidatedConsumer {

    final CancelOrderUseCase cancelOrderUseCase;
    final DispatchOrderUseCase dispatchOrderUseCase;

    public void handler(OrderValidatedEvent orderValidatedEvent) {

        if (orderValidatedEvent.canBeSatisfied()) {
            dispatchOrderUseCase.dispatchOrder(orderValidatedEvent.orderId());
        } else {
            cancelOrderUseCase.canselOrder(orderValidatedEvent.orderId());
        }

    }
}
