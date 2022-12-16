package io.github.jlmc.orders.domain.model.commands;

import java.util.List;

public record CreateOrderCommand(List<Item> items) {
    public CreateOrderCommand {
        if (items == null || items.isEmpty()) throw new IllegalArgumentException("The items cant be null or empty");
        items = List.copyOf(items);
    }
}
