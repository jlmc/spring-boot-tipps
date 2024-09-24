package io.github.jlmc.poc.api.orders;

import io.github.jlmc.poc.api.orders.inputs.CreateOrderRequest;
import io.github.jlmc.poc.api.orders.outputs.OrderRepresentation;
import io.github.jlmc.poc.domain.orders.commands.CreateOrderCommand;
import io.github.jlmc.poc.domain.orders.entities.OrderId;
import io.github.jlmc.poc.domain.orders.ports.incoming.CreateOrderReservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/orders")
public class OrdersController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrdersController.class);

    @Autowired
    CreateOrderReservation createOrderReservation;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderRepresentation create(@RequestBody @Validated CreateOrderRequest payload) {
        LOGGER.info("Creating order: {}", payload);

        CreateOrderCommand command = payload.toCommand();

        OrderId orderId = createOrderReservation.createOrder(command);
        return new OrderRepresentation(orderId.toString());
    }
}
