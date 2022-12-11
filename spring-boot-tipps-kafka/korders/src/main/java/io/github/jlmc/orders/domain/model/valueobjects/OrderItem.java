package io.github.jlmc.orders.domain.model.valueobjects;

import io.github.jlmc.orders.domain.model.entities.Product;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of", access = AccessLevel.PUBLIC)
public class OrderItem {
    private Product product;
    private Integer qty;
}
