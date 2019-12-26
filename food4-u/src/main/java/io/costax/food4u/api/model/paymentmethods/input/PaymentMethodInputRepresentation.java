package io.costax.food4u.api.model.paymentmethods.input;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class PaymentMethodInputRepresentation {

    @NotBlank
    private String name;
}
