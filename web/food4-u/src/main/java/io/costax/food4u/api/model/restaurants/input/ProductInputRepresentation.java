package io.costax.food4u.api.model.restaurants.input;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@ApiModel(value = "ProductInput")
@Getter
@Setter
public class ProductInputRepresentation {

    @ApiModelProperty(example = "Feijoada de chocos", required = true)
    @NotBlank
    private String name;

    @ApiModelProperty(example = "Feijoada à moda da tia Maria", required = true)
    @NotBlank
    private String description;

    @ApiModelProperty(example = "10.95")
    @NotNull
    @PositiveOrZero
    private BigDecimal price;

    @ApiModelProperty(example = "true")
    @NotNull
    private Boolean active;
}
