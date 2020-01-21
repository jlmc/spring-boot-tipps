package io.costax.food4u.api.model.restaurants.output;

import io.costax.food4u.api.model.cookers.output.CookerOutputRepresentation;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;

@ApiModel(value = "RestaurantOutput")
@Data
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestaurantOutputRepresentation {

    private Long id;
    private String name;
    private BigDecimal takeAwayTax = BigDecimal.ZERO;
    private CookerOutputRepresentation cooker;
    private AddressOutputRepresentation address;
    private boolean active;

}
