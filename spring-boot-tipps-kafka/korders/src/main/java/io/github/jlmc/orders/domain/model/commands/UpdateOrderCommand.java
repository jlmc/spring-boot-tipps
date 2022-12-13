package io.github.jlmc.orders.domain.model.commands;

import java.util.List;

public record UpdateOrderCommand(String orderId, List<Item> items) {
    public UpdateOrderCommand {
        if (orderId == null || orderId.isBlank()) throw new IllegalArgumentException("The order id can't be blank");
        items = List.copyOf(items);
    }
}
