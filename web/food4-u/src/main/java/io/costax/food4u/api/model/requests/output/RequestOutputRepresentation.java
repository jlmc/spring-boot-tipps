package io.costax.food4u.api.model.requests.output;

import io.costax.food4u.api.model.paymentmethods.output.PaymentMethodOutputRepresentation;
import io.costax.food4u.api.model.restaurants.output.AddressOutputRepresentation;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@ApiModel(value = "RequestOutput")
@Getter
@Setter
public class RequestOutputRepresentation extends RepresentationModel<RequestOutputRepresentation> {

    private String code;
    private BigDecimal subTotal;
    private BigDecimal takeAwayTax;
    private BigDecimal totalValue;
    private String status;
    private OffsetDateTime createdAt;

    private RestaurantOutputRepresentation restaurant;
    private UserOutputRepresentation client;
    private PaymentMethodOutputRepresentation paymentMethod;
    private AddressOutputRepresentation deliveryAddress;
    private List<RequestItemOutputRepresentation> items = new ArrayList<>();

}