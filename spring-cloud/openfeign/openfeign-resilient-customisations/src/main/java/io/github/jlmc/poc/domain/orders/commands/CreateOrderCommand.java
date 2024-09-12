package io.github.jlmc.poc.domain.orders.commands;


import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.Iterator;
import java.util.Set;

@Valid
public record CreateOrderCommand(
        @NotNull @NotEmpty Set<@Valid Item> items) implements Iterable<CreateOrderCommand.Item> {

    @Override
    public Iterator<Item> iterator() {
        return items.iterator();
    }

    public record Item(@NotBlank @Pattern(regexp = "^\\d+$") String productId, @NotNull @Positive Integer quantity) {
    }
}
