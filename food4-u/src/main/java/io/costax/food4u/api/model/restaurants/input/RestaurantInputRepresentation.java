package io.costax.food4u.api.model.restaurants.input;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Getter
@Setter
public class RestaurantInputRepresentation {

    @NotBlank
    private String name;

    @NotNull
    @PositiveOrZero
    private BigDecimal takeAwayTax = BigDecimal.ZERO;

    @Valid
    @NotNull
    private CookerInputRepresentation cooker;

    private AddressInputRepresentation address;
}
