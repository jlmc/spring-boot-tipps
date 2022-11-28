package io.costax.food4u.api.model.requests.input;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@ApiModel(value = "RestaurantIdInput")
@Getter
@Setter
class RestaurantRepresentation {

    @ApiModelProperty(example = "1")
    @NotNull
    private Long id;
}
