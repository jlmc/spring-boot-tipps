package io.github.jlmc.orders.domain.model.commands;

import java.util.List;

public record UpdateOrderCommand(String orderId, List<Item> items) {
}
