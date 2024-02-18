package io.github.jlmc.uof.domain.fruits.ports.outgoing;

import io.github.jlmc.uof.domain.fruits.entities.PriceDetail;

public interface MarketProductInformationProvider {

    PriceDetail getProductPrice(String productId);

    String createOrderReservation(OrderReservationRequest request);

    record OrderReservationRequest(String productId,
                                   String sellerId,
                                   String priceDetailHash,
                                   String clientNif) {}

    record OrderReservationResponse(String reservationId) {}
}
