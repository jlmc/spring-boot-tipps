package io.costax.demo.domain.repositories;

import io.costax.demo.domain.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Integer> {

    @Query("select distinct sc from ShoppingCart sc left join fetch sc.items i left join fetch i.book where sc.id = :userId")
    Optional<ShoppingCart> getById(Integer userId);

}
