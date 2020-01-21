package io.costax.food4u.api.model.restaurants.output;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@ApiModel(value = "ProductOutput")
@Getter
@Setter
public class ProductOutputRepresentation {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Boolean active;
}
