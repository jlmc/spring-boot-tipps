package io.github.jlmc.pizzacondo.om.service.adapter.inbound.messaging;

import io.github.jlmc.pizzacondo.common.messages.OrderFinishedPreparationEvent;
import io.github.jlmc.pizzacondo.om.service.application.port.input.InDeliveryOrderUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderFinishedPreparationConsumer {

    @Autowired
    InDeliveryOrderUseCase inDeliveryOrderUseCase;

    public void handler(OrderFinishedPreparationEvent event) {
        log.info("OrderFinishedPreparationEvent received: {}", event);
        inDeliveryOrderUseCase.inDelivery(event.orderId());
    }
}
