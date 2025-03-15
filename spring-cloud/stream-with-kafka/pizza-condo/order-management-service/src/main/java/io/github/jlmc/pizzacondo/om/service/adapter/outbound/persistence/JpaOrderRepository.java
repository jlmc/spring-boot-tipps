package io.github.jlmc.pizzacondo.om.service.adapter.outbound.persistence;

import io.github.jlmc.pizzacondo.om.service.application.port.output.OrderRepository;
import io.github.jlmc.pizzacondo.om.service.domain.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaOrderRepository extends JpaRepository<OrderEntity, Long>, OrderRepository {

    default Order add(Order order) {
        OrderEntity entity = new OrderEntity(order);
        OrderEntity saved = saveAndFlush(entity);

        return order.toBuilder()
                .id("" + saved.getId())
                .placedAt(saved.getPlacedAt())
                .build();
    }

    @Override
    default Optional<Order> lookup(String orderId) {
        return findById(Long.valueOf(orderId))
                .map(OrderEntity::toOrder);
    }

    default void update(Order order) {
        findById(Long.valueOf(order.getId()))
                .ifPresent(entity -> {
                    entity.update(order);
                    flush();
                });
    }
}
