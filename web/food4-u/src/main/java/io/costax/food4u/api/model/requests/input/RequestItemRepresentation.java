package io.costax.food4u.api.model.requests.input;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@ApiModel(value = "RequestItem", description = "Request Item Input representation")
@Getter
@Setter
class RequestItemRepresentation {

    @ApiModelProperty(example = "1")
    @NotNull
    private Long productId;

    @ApiModelProperty(example = "3")
    @NotNull
    @PositiveOrZero
    private Integer qty;

    @ApiModelProperty(example = "Lorem ipsum dolor sit amet, consectetur adipiscing elit")
    private String observations;
}
