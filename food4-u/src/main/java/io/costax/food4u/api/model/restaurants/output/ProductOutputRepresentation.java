package io.costax.food4u.api.model.restaurants.output;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductOutputRepresentation {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Boolean active;
}
