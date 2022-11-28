package io.costax.springemaildemos.domain.orders.control;

import io.costax.springemaildemos.domain.orders.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
