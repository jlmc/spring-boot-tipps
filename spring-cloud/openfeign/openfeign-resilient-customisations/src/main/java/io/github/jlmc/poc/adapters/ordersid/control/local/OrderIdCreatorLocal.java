package io.github.jlmc.poc.adapters.ordersid.control.local;

import io.github.jlmc.poc.domain.orders.entities.OrderId;
import io.github.jlmc.poc.domain.orders.ports.outgoing.OrderIdCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class OrderIdCreatorLocal implements OrderIdCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderIdCreatorLocal.class);

    @Override
    public OrderId generateOrderId() {
        LOGGER.info("Generating order ID");
        String id = UUID.randomUUID().toString();
        OrderId orderId = new OrderId(id);
        LOGGER.info("Generated Order ID: {}", orderId);

        return orderId;
    }
}
