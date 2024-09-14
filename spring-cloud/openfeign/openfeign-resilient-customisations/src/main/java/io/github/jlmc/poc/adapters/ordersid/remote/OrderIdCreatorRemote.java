package io.github.jlmc.poc.adapters.ordersid.remote;

import io.github.jlmc.poc.domain.orders.entities.OrderId;
import io.github.jlmc.poc.domain.orders.ports.outgoing.OrderIdCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderIdCreatorRemote implements OrderIdCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderIdCreatorRemote.class);


    private final OrderIdGeneratorServiceApiClient orderIdGeneratorServiceApiClient;

    public OrderIdCreatorRemote(OrderIdGeneratorServiceApiClient orderIdGeneratorServiceApiClient) {
        this.orderIdGeneratorServiceApiClient = orderIdGeneratorServiceApiClient;
    }

    @Override
    public OrderId generateOrderId() {
        LOGGER.info("Generating order ID");
        OrderId orderId = orderIdGeneratorServiceApiClient.generateOrderId();
        LOGGER.info("Generated order ID: {}", orderId);
        return orderId;
    }
}
