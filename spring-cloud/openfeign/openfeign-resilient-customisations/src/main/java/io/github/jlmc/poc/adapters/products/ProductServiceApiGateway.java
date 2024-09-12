package io.github.jlmc.poc.adapters.products;

import io.github.jlmc.poc.domain.orders.entities.Product;
import io.github.jlmc.poc.domain.orders.ports.outgoing.ProductProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductServiceApiGateway implements ProductProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceApiGateway.class);

    @Autowired
    ProductsServiceClient productsServiceClient;

    @Override
    public Product getProduct(Integer productId) {
        LOGGER.info("Retrieving product with id {}", productId);
        return productsServiceClient.product(productId);
    }
}
