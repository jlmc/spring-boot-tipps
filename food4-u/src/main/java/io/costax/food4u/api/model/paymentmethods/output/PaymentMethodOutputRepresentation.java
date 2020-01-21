package io.costax.food4u.api.model.paymentmethods.output;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@ApiModel(value = "PaymentMethodOutput")
@Getter
@Setter
public class PaymentMethodOutputRepresentation {
    private Long id;
    private String name;
}
