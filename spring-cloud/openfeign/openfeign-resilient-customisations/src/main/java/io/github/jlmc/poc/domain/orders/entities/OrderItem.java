package io.github.jlmc.poc.domain.orders.entities;

import java.math.BigDecimal;

public record OrderItem(

        BigDecimal totalPrice,

        int quantity,

        Integer productId,
        String productName,
        BigDecimal productPrice,
        String productDescription
) {

    public static OrderItem orderItem(int quantity, Product product) {
        BigDecimal price = product.price();
        BigDecimal totalPrice = price.multiply(BigDecimal.valueOf(quantity));

        return new OrderItem(totalPrice, quantity, product.id(), product.name(), product.price(), product.description());
    }

}
