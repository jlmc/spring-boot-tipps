package io.github.jlmc.pizzacondo.common.messages;

public record OrderValidatedEvent(
        String orderId,
        boolean canBeSatisfied
) {
}
