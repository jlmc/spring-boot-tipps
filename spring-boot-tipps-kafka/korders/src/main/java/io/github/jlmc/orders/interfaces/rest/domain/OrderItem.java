package io.github.jlmc.orders.interfaces.rest.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItem(
        @NotBlank String productId, @NotNull @Positive Integer qty) {

    @Override
    public String toString() {
        return "<" + productId + ":" + qty + ">";
    }
}
