package io.costax.demo.api.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
public class ShoppingCartItemRequest {

    @NotNull
    private Integer bookId;
    @NotNull
    @Positive
    private Integer qty;
}
