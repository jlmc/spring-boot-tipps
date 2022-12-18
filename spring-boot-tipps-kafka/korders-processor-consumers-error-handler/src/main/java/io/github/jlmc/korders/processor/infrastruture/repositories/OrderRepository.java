package io.github.jlmc.korders.processor.infrastruture.repositories;

import io.github.jlmc.korders.processor.domain.model.aggregates.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    @Query("select o from Order o left join fetch o.items where o.id = :id")
    Optional<Order> findOrderByIdFetchItems(String id);
}
