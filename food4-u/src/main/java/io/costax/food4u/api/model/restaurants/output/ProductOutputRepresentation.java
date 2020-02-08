package io.costax.food4u.api.model.restaurants.output;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;

@ApiModel(value = "ProductOutput")
@Getter
@Setter
public class ProductOutputRepresentation extends RepresentationModel<ProductOutputRepresentation> {

    // extends RepresentationModel<RestaurantOutputRepresentation>

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Boolean active;
}
