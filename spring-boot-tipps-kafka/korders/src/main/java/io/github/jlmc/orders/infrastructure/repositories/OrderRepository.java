package io.github.jlmc.orders.infrastructure.repositories;

import io.github.jlmc.orders.domain.model.aggregates.Order;
import io.github.jlmc.orders.domain.model.entities.Product;
import io.github.jlmc.orders.domain.model.valueobjects.OrderItem;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;

@Repository
public class OrderRepository {

    private Map<String, Order> orders = new HashMap<>();

    public Optional<Order> findById(String orderId) {
        var order = Order.builder()
                         .id(orderId)
                         .created(Instant.now())
                         .orderItems(List.of(
                                         OrderItem.of(Product.of("1", "i-Phone"), 2),
                                         OrderItem.of(Product.of("2", "Macbook-pro 2022"), 1)
                                 )
                         )
                         .build();

        return Optional.of(order);
    }

    public Order save(Order newOrder) {
        orders.put(newOrder.getId(), newOrder);
        return orders.get(newOrder.getId());
    }
}
