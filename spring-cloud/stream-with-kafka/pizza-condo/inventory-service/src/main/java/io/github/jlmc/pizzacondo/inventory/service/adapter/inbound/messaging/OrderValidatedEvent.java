package io.github.jlmc.pizzacondo.inventory.service.adapter.inbound.messaging;

public record OrderValidatedEvent(
        String orderId,
        boolean canBeSatisfied
) {
}
