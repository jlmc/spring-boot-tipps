package io.github.jlmc.uof.domain.fruits.commands;

public record CreateOrderReservationCommand(String productId,
                                            String sellerId,
                                            String priceDetailHash,
                                            String clientNif) {
}
