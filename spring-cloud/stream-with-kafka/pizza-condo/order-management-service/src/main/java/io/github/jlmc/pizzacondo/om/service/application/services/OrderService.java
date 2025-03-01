package io.github.jlmc.pizzacondo.om.service.application.services;

import io.github.jlmc.pizzacondo.om.service.application.port.input.PlaceOrderUseCase;
import io.github.jlmc.pizzacondo.om.service.domain.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrderService implements PlaceOrderUseCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    @Override
    public Order placeOrder(PlaceOrderCommand command) {
        Order order = new Order();
        return order;
    }
}
