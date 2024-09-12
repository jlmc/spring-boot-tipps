package io.github.jlmc.poc.domain.orders.control;

import io.github.jlmc.poc.domain.orders.entities.OrderId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrderIdCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderIdCreator.class);

    public OrderId generateOrderId() {
        LOGGER.info("Generating order ID");
        String id = UUID.randomUUID().toString();
        OrderId orderId = new OrderId(id);
        LOGGER.info("Generated Order ID: {}", orderId);

        return orderId;
    }
}
