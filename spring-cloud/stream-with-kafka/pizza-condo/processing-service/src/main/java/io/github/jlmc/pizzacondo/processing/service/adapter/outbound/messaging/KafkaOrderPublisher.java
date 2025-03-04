package io.github.jlmc.pizzacondo.processing.service.adapter.outbound.messaging;

import io.github.jlmc.pizzacondo.common.messages.OrderFinishedPreparationEvent;
import io.github.jlmc.pizzacondo.common.messages.OrderStartedPreparationEvent;
import io.github.jlmc.pizzacondo.processing.service.application.port.output.NotificationService;
import io.github.jlmc.pizzacondo.processing.service.domain.model.Order;
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

    public static final String ORDER_STARTED_PREPARATION_OUT_0 = "orderStartedPreparation-out-0";
    private static final String ORDER_FINISHED_PREPARATION_OUT_0 = "orderFinishedPreparation-out-0";
    private final StreamBridge streamBridge;


    @Override
    public void notifyStartedPreparation(Order order) {

        OrderStartedPreparationEvent payload =
                new OrderStartedPreparationEvent(
                        order.getId(),
                        order.getPlacedAt(),
                        order.getCustomerId(),
                        order.getSize(),
                        order.getToppings());

        Message<OrderStartedPreparationEvent> message = MessageBuilder
                .withPayload(payload)
                .setHeader(KafkaHeaders.KEY, order.getId())
                .build();
        log.info("Sending notifyStartedPreparation orderId: {}", order.getId());

        streamBridge.send(ORDER_STARTED_PREPARATION_OUT_0, message);

    }

    @Override
    public void notifyFinishedPreparation(Order order) {

        OrderFinishedPreparationEvent payload =
                new OrderFinishedPreparationEvent(
                        order.getId(),
                        order.getPlacedAt(),
                        order.getCustomerId(),
                        order.getSize(),
                        order.getToppings());

        Message<OrderFinishedPreparationEvent> message = MessageBuilder
                .withPayload(payload)
                .setHeader(KafkaHeaders.KEY, order.getId())
                .build();
        log.info("Sending notifyFinishedPreparation orderId: {}", order.getId());

        streamBridge.send(ORDER_FINISHED_PREPARATION_OUT_0, message);
    }
}
