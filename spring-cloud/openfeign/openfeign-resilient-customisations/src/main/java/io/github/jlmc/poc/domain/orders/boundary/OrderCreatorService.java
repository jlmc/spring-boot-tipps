package io.github.jlmc.poc.domain.orders.boundary;

import io.github.jlmc.poc.domain.orders.entities.Product;
import io.github.jlmc.poc.domain.orders.ports.outgoing.ProductProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderCreatorService {

    @Autowired
    ProductProvider productProvider;

    public Product createOrder() {
        return productProvider.getProduct(1);
    }
}
