package io.github.jlmc.pizzacondo.common.messages;

import java.time.Instant;
import java.util.List;

public record OrderAcceptedEvent(
        String orderId,
        Instant placedAt,
        String customerId,
        int size,
        List<String> toppings,
        String status
) {
}

