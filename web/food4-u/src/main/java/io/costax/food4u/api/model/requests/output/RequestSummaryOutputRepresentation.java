package io.costax.food4u.api.model.requests.output;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@ApiModel(value = "RequestSummary")
@Getter
@Setter
public class RequestSummaryOutputRepresentation {

    private String code;
    private BigDecimal subTotal;
    private BigDecimal takeAwayTax;
    private BigDecimal totalValue;
    private String status;
    private OffsetDateTime createdAt;

    private String restaurantName;
    private String clientName;
}
