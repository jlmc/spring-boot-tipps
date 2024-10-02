package io.github.jlmc.poc.adapters.products.boundary;

import feign.FeignException;
import io.github.jlmc.poc.adapters.products.control.ProductsServiceClient;
import io.github.jlmc.poc.api.orders.ex.ProductNotFoundException;
import io.github.jlmc.poc.domain.orders.entities.Product;
import io.github.jlmc.poc.domain.orders.ports.outgoing.ProductProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProductServiceApiGateway implements ProductProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceApiGateway.class);

    @Autowired
    ProductsServiceClient productsServiceClient;

    @Override
    public Product getProduct(Integer productId) {
        LOGGER.info("Retrieving product with id {}", productId);
        try {
            return productsServiceClient.product(UUID.randomUUID().toString(), productId);
        } catch (FeignException.NotFound e) {
            throw new ProductNotFoundException("Product with the id [%s] not found.".formatted(productId), e);
        }
    }
}
