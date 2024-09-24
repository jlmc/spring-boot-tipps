package io.github.jlmc.poc.domain.orders.commands;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.util.List;

@Valid
public record CreateOrderCommand(
        @Valid
        @NotEmpty List<Item> items) {

    public record Item(@NotBlank @Pattern(regexp = "^\\d+$") String productId, @NotNull @Positive Integer quantity) {
    }
}
