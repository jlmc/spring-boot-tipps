package io.github.jlmc.pizzacondo.om.service.adapter.inbound.messaging;

public record OrderValidatedEvent(
        String orderId,
        boolean canBeSatisfied
) {
}
