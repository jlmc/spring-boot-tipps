package io.github.jlmc.pizzacondo.om.service.adapter.outbound.messaging;

import io.github.jlmc.pizzacondo.common.messages.OrderAcceptedEvent;
import io.github.jlmc.pizzacondo.common.messages.OrderPlacedEvent;
import io.github.jlmc.pizzacondo.om.service.application.port.output.NotificationService;
import io.github.jlmc.pizzacondo.om.service.domain.model.Order;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class KafkaOrderPublisher implements NotificationService {

    private final StreamBridge streamBridge;

    @Override
    public void orderPlaced(Order order) {
        OrderPlacedEvent payload =
                new OrderPlacedEvent(
                        order.getId(),
                        order.getPlacedAt(),
                        order.getCustomerId(),
                        order.getSize(),
                        order.toppingsAsString(),
                        order.getStatus().toString());

        Message<OrderPlacedEvent> message = MessageBuilder
                .withPayload(payload)
                .setHeader(KafkaHeaders.KEY, order.getId())
                .build();

        log.info("Sending message to Kafka order placed event orderId: {}", order.getId());

        streamBridge.send("orderReceived-out-0", message);
    }

    @Override
    public void orderAccepted(Order order) {
        OrderAcceptedEvent payload =
                new OrderAcceptedEvent(
                        order.getId(),
                        order.getPlacedAt(),
                        order.getCustomerId(),
                        order.getSize(),
                        order.toppingsAsString(),
                        order.getStatus().toString());

        Message<OrderAcceptedEvent> message = MessageBuilder
                .withPayload(payload)
                .setHeader(KafkaHeaders.KEY, order.getId())
                .build();
        log.info("Sending message to Kafka order accepted event orderId: {}", order.getId());

        streamBridge.send("orderAccepted-out-0", message);
    }
}
