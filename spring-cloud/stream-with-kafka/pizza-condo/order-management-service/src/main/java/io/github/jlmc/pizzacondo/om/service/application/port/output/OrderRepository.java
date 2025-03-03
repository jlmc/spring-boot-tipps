package io.github.jlmc.pizzacondo.om.service.application.port.output;

import io.github.jlmc.pizzacondo.om.service.domain.model.Order;

import java.util.Optional;

public interface OrderRepository {

    Order add(Order order);

    Optional<Order> lookup(String id);

    void update(Order order);
}
