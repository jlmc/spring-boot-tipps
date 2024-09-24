package io.github.jlmc.poc.domain.orders.ports.outgoing;

import io.github.jlmc.poc.domain.orders.entities.Order;

public interface OrderRepository {

    Order save(Order order);
}
