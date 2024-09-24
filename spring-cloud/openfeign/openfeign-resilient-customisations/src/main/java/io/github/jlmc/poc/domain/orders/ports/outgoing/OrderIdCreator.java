package io.github.jlmc.poc.domain.orders.ports.outgoing;

import io.github.jlmc.poc.domain.orders.entities.OrderId;

public interface OrderIdCreator {
    OrderId generateOrderId();
}
