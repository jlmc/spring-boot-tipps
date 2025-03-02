package io.github.jlmc.pizzacondo.om.service.adapter.outbound.messaging;

import io.github.jlmc.pizzacondo.om.service.application.port.output.NotificationService;
import io.github.jlmc.pizzacondo.om.service.domain.model.Order;
import lombok.AllArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class KafkaOrderPublisher implements NotificationService {

    private final StreamBridge streamBridge;

    @Override
    public void orderPlaced(Order order) {
        OrderPlacedEvent payload = new OrderPlacedEvent(order);

        Message<OrderPlacedEvent> message = MessageBuilder
                .withPayload(payload)
                .setHeader(KafkaHeaders.KEY, order.getId())
                .build();

        streamBridge.send("flightFinished-out-0", message);
    }
}
