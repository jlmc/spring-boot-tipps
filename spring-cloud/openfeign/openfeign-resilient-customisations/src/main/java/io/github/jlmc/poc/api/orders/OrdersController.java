package io.github.jlmc.poc.api.orders;

import io.github.jlmc.poc.api.orders.inputs.CreateOrderRequest;
import io.github.jlmc.poc.domain.orders.boundary.OrderCreatorService;
import io.github.jlmc.poc.domain.orders.entities.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/orders")
public class OrdersController {

    @Autowired
    OrderCreatorService orderCreatorService;

    @PostMapping
    public Product create(@RequestBody @Validated CreateOrderRequest payload) {
        return orderCreatorService.createOrder();
    }
}
