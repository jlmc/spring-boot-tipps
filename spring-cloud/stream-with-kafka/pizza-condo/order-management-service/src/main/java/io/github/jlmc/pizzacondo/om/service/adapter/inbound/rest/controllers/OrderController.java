package io.github.jlmc.pizzacondo.om.service.adapter.inbound.rest.controllers;

import io.github.jlmc.pizzacondo.om.service.adapter.inbound.rest.model.OrderRequest;
import io.github.jlmc.pizzacondo.om.service.adapter.inbound.rest.model.OrderResponse;
import io.github.jlmc.pizzacondo.om.service.application.port.input.PlaceOrderUseCase;
import io.github.jlmc.pizzacondo.om.service.domain.model.Order;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/orders")
public class OrderController {

    static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    private final PlaceOrderUseCase placeOrderUseCase;

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@Validated @RequestBody OrderRequest request) {
        LOGGER.info("Order placed: {}", request);

        Order order = placeOrderUseCase.placeOrder(request.toCommand());

        return ResponseEntity.accepted().body(OrderResponse.fromOrder(order));
    }


}
