package io.costax.food4u.api.model.restaurants.input;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel(value = "RestaurantAddress")
@Getter
@Setter
public class AddressInputRepresentation {

    @ApiModelProperty(example = "Cezamo street", required = true)
    private String street;

    @ApiModelProperty(example = "New York")
    private String city;

    @ApiModelProperty(example = "38400-000", required = true)
    private String zipCode;
}
