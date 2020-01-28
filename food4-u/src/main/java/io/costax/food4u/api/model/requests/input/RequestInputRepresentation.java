package io.costax.food4u.api.model.requests.input;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@ApiModel(value = "RequestInput")
@Getter
@Setter
public class RequestInputRepresentation {

    @Valid
    @NotNull
    private RestaurantRepresentation restaurant;

    @Valid
    @NotNull
    private PaymentMethodRepresentation paymentMethod;

    @Valid
    @NotNull
    private DeliveryAddressRepresentation deliveryAddress;

    @Valid
    @NotEmpty
    @NotNull
    private List<RequestItemRepresentation> items;
}
