package io.github.jlmc.uof.api.representations;

import io.github.jlmc.uof.domain.fruits.commands.CreateOrderReservationCommand;
import jakarta.validation.constraints.NotBlank;

public record CreateReservationRequest(@NotBlank String productId,
                                       @NotBlank String sellerId,
                                       @NotBlank String priceDetailHash,
                                       @NotBlank String clientNif
) {

    public CreateOrderReservationCommand toOrderReservationCommand() {
        return new CreateOrderReservationCommand(productId, sellerId, priceDetailHash, clientNif);
    }
}
