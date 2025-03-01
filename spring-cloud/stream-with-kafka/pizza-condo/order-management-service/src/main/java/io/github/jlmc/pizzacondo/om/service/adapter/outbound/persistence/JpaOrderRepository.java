package io.github.jlmc.pizzacondo.om.service.adapter.outbound.persistence;

import io.github.jlmc.pizzacondo.om.service.application.port.output.OrderRepository;
import io.github.jlmc.pizzacondo.om.service.domain.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaOrderRepository extends JpaRepository<OrderEntity, Long>, OrderRepository {

    default Order add(Order order) {
        OrderEntity entity = new OrderEntity(order);
        OrderEntity saved = saveAndFlush(entity);

        return order.toBuilder()
                .id("" + saved.getId())
                .placedAt(saved.getPlacedAt())
                .build();
    }
}
