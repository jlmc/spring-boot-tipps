package io.github.jlmc.orders.infrastructure.repositories;

import io.github.jlmc.orders.domain.model.entities.Product;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public class ProductRepository {

    private final List<Product> products = new ArrayList<>();

    @PostConstruct
    public void populates() {
        Stream.of(
                Product.of("1", "I-Phone"),
                Product.of("2", "MacBook-Pro 2020"),
                Product.of("UNKNOWN", "UNKNOWN")
        ).forEach(products::add);
    }

    public Optional<Product> findById(String id) {
        return this.products.stream().filter(it -> Objects.equals(it.getId(), id)).findFirst();
    }
}
