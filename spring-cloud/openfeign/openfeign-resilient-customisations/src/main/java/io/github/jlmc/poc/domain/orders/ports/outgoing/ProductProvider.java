package io.github.jlmc.poc.domain.orders.ports.outgoing;

import io.github.jlmc.poc.domain.orders.entities.Product;

public interface ProductProvider {

    Product getProduct(Integer productId);
}
