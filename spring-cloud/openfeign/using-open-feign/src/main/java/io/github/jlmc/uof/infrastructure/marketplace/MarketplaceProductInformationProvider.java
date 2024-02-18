package io.github.jlmc.uof.infrastructure.marketplace;

import io.github.jlmc.uof.domain.fruits.entities.PriceDetail;
import io.github.jlmc.uof.domain.fruits.ports.outgoing.MarketProductInformationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MarketplaceProductInformationProvider implements MarketProductInformationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(MarketplaceProductInformationProvider.class);

    private final MarketplaceRequester marketplaceRequester;

    public MarketplaceProductInformationProvider(MarketplaceRequester marketplaceRequester) {
        this.marketplaceRequester = marketplaceRequester;
    }

    @Override
    public PriceDetail getProductPrice(String productId) {
        LOGGER.info("Calling the Marketplace to get the best price of the product {}", productId);
        PriceDetail productBestPrice = marketplaceRequester.getProductBestPrice(productId);

        return productBestPrice;
    }

    @Override
    public String createOrderReservation(OrderReservationRequest request) {
        LOGGER.info("Calling the Marketplace to create a new order reservation");

        OrderReservationResponse reservation = marketplaceRequester.createReservation(request);

        LOGGER.info("Called the Marketplace to create a new order reservation: {}", reservation.reservationId());

        return reservation.reservationId();
    }
}
