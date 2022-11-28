package io.costax.food4u.api.model.restaurants.input;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@ApiModel(value = "RestaurantInput", description = "Restaurant Input representation")
@Getter
@Setter
public class RestaurantInputRepresentation {

    @ApiModelProperty(example = "Quinta Dona Maria", required = true)
    @NotBlank
    private String name;

    @ApiModelProperty(example = "3.50", required = true)
    @NotNull
    @PositiveOrZero
    private BigDecimal takeAwayTax = BigDecimal.ZERO;

    @Valid
    @NotNull
    private CookerInputRepresentation cooker;

    private AddressInputRepresentation address;
}
