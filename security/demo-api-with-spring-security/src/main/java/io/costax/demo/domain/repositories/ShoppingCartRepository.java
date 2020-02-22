package io.costax.demo.domain.repositories;

import io.costax.demo.domain.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Integer> {

}
