package io.github.jlmc.orders.domain.model.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class Product {
    private String id;
    private String name;
}
