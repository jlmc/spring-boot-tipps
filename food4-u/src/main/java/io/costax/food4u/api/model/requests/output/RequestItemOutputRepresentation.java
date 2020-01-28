package io.costax.food4u.api.model.requests.output;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@ApiModel(value = "RequestItemOutput")
@Getter
@Setter
class RequestItemOutputRepresentation {

    private Long productId;
    private String productName;
    private Integer qty;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String observations;
}
