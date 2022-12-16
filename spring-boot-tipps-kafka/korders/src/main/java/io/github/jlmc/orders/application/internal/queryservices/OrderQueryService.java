package io.github.jlmc.orders.application.internal.queryservices;

import io.github.jlmc.orders.domain.model.aggregates.Order;
import io.github.jlmc.orders.infrastructure.repositories.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class OrderQueryService {

    private OrderRepository orderRepository;

    public Optional<Order> findById(String orderId) {
        return orderRepository.findById(orderId);
    }

}
