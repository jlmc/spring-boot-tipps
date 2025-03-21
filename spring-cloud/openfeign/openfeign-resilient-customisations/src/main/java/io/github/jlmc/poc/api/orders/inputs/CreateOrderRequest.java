package io.github.jlmc.poc.api.orders.inputs;

import io.github.jlmc.poc.domain.orders.commands.CreateOrderCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.stream.Collectors;

@Valid
public record CreateOrderRequest(
        @NotEmpty List<@Valid Item> items
) {
    public CreateOrderCommand toCommand() {
        if (items == null) {
            throw new IllegalArgumentException("items cannot be null");
        }

        if (this.items.isEmpty()) {
            throw new IllegalArgumentException("Items cannot be empty");
        }

        List<CreateOrderCommand.Item> commandItems = items.stream()
                .map(it -> new CreateOrderCommand.Item(it.productId(), it.quantity()))
                .collect(Collectors.toList());

        return new CreateOrderCommand(commandItems);
    }
}
