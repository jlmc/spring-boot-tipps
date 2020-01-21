package io.costax.food4u.api.model.requests.input;


import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
class DeliveryAddressPresentation {

    @ApiModelProperty(example = "Cezamo street", required = true)
    @NotBlank
    private String street;

    @ApiModelProperty(example = "New York")
    @NotBlank
    private String city;

    @ApiModelProperty(example = "38400-000", required = true)
    @NotBlank
    private String zipCode;

}
