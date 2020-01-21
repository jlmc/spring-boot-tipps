package io.costax.food4u.api.model.restaurants.input;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@ApiModel(value = "RestaurantCookerId")
@Getter
@Setter
public class CookerInputRepresentation {

    @ApiModelProperty(example = "1")
    @NotNull
    private Long id;
}
