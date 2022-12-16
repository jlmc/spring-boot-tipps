package io.github.jlmc.orders.application.internal.commandservices;

import io.github.jlmc.orders.domain.model.entities.Product;
import io.github.jlmc.orders.domain.model.exceptions.IllegalProductIdArgumentException;
import io.github.jlmc.orders.infrastructure.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;


@AllArgsConstructor
@Component
public class ProductResolver {

    private ProductRepository productRepository;

    Product productOf(String productId) {
        return productRepository.findById(productId)
                                .orElseThrow(() -> new IllegalProductIdArgumentException("Invalid product id " + productId));
    }
}
