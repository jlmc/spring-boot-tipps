package io.github.jlmc.pizzacondo.processing.service.adapter.inbound.messaging;

public record OrderValidatedEvent(
        String orderId,
        boolean canBeSatisfied
) {
}
