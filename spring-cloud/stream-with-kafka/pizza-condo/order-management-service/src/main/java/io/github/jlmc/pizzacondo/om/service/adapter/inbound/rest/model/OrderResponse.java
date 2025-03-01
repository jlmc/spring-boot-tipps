package io.github.jlmc.pizzacondo.om.service.adapter.inbound.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.jlmc.pizzacondo.om.service.domain.model.Order;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderResponse(String orderId, String status) {

    public static OrderResponse fromOrder(Order order) {
        return new OrderResponse(order.getId(), order.getStatus().toString());
    }
}
