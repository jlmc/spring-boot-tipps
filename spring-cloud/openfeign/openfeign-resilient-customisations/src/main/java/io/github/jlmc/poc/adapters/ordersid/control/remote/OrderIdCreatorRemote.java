package io.github.jlmc.poc.adapters.ordersid.control.remote;

import io.github.jlmc.poc.domain.orders.entities.OrderId;
import io.github.jlmc.poc.domain.orders.ports.outgoing.OrderIdCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class OrderIdCreatorRemote implements OrderIdCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderIdCreatorRemote.class);


    private final OrderIdGeneratorServiceApiClient orderIdGeneratorServiceApiClient;

    public OrderIdCreatorRemote(OrderIdGeneratorServiceApiClient orderIdGeneratorServiceApiClient) {
        this.orderIdGeneratorServiceApiClient = orderIdGeneratorServiceApiClient;
    }

    @Override
    public OrderId generateOrderId() {
        LOGGER.info("Generating order ID");

        Map<String, String> duke = Map.of("Duke", "Foo");

        OrderId orderId = orderIdGeneratorServiceApiClient.generateOrderId(duke);
        LOGGER.info("Generated order ID: {}", orderId);
        return orderId;
    }
}
