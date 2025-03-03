package io.github.jlmc.pizzacondo.om.service.adapter.outbound.messaging;

import io.github.jlmc.pizzacondo.om.service.domain.model.Order;
import io.github.jlmc.pizzacondo.om.service.domain.model.OrderStatus;
import io.github.jlmc.pizzacondo.om.service.domain.model.Topping;

import java.time.Instant;
import java.util.List;

public record OrderAcceptedEvent(
        String orderId,
        Instant placedAt,
        String customerId,
        int size,
        List<Topping> toppings,
        OrderStatus status
) {
    public OrderAcceptedEvent(Order order) {
        this(order.getId(), order.getPlacedAt(), order.getCustomerId(), order.getSize(), order.getToppings(), order.getStatus());
    }
}

