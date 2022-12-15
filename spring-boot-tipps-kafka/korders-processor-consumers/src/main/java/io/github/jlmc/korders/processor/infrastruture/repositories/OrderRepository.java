package io.github.jlmc.korders.processor.infrastruture.repositories;

import io.github.jlmc.korders.processor.domain.model.aggregates.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
}
