package io.github.jlmc.pizzacondo.om.service.adapter.inbound.messaging;

import io.github.jlmc.pizzacondo.common.messages.OrderStartedPreparationEvent;
import io.github.jlmc.pizzacondo.om.service.application.port.input.InProcessOrderUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderStartedPreparationConsumer {

    @Autowired
    InProcessOrderUseCase inProcessOrderUseCase;

    public void handler(OrderStartedPreparationEvent event) {
        log.info("handing OrderStartedPreparationEvent: {}", event);
        inProcessOrderUseCase.inProcess(event.orderId());
    }
}
