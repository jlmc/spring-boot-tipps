package io.github.jlmc.pizzacondo.processing.service.adapter.inbound.messaging;

import java.time.Instant;
import java.util.List;

public record ScheduleOrderPreparationEvent(
        String orderId,
        Instant placedAt,
        String customerId,
        int size,
        List<String> toppings,
        String status
) {
}

