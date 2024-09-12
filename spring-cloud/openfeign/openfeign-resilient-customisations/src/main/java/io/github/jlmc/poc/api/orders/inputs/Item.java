package io.github.jlmc.poc.api.orders.inputs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record Item(@NotBlank @Pattern(regexp = "^\\d+$") String productId, @NotNull @Positive Integer quantity) {
}
