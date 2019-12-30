package io.costax.food4u.api.model.requests.output;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
class RequestItemOutputPresentation {

    private Long productId;
    private String productName;
    private Integer qty;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String observations;
}
