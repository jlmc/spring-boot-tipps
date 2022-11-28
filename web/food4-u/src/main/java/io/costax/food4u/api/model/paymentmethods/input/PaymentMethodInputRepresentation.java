package io.costax.food4u.api.model.paymentmethods.input;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ApiModel(value = "PaymentMethodInput", description = "Payment Method Input Representation")
public class PaymentMethodInputRepresentation {

    @NotBlank
    @ApiModelProperty(example = "CREDIT CARD")
    private String name;
}
