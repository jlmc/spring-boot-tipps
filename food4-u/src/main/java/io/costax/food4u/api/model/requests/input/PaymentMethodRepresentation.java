package io.costax.food4u.api.model.requests.input;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
class PaymentMethodRepresentation {

    @NotNull
    private Long id;
}
