package io.github.jlmc.poc.adapters.orders;

import io.github.jlmc.poc.domain.orders.entities.Order;
import io.github.jlmc.poc.domain.orders.ports.outgoing.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class NoOpOrderRepository implements OrderRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(NoOpOrderRepository.class);

    @Override
    public Order save(Order order) {
        LOGGER.info("Saving order: {}", order);
        return order;
    }
}
