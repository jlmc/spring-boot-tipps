package io.costax.food4u.api.model.requests.input;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
class RequestItemRepresentation {

    @NotNull
    private Long productId;

    @NotNull
    @PositiveOrZero
    private Integer qty;

    private String observations;
}
