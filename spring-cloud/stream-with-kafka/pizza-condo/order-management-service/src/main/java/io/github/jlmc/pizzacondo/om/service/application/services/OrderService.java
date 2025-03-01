package io.github.jlmc.pizzacondo.om.service.application.services;

import io.github.jlmc.pizzacondo.om.service.application.port.input.PlaceOrderUseCase;
import io.github.jlmc.pizzacondo.om.service.application.port.output.OrderRepository;
import io.github.jlmc.pizzacondo.om.service.domain.model.Order;
import io.github.jlmc.pizzacondo.om.service.domain.model.OrderStatus;
import io.github.jlmc.pizzacondo.om.service.domain.model.Topping;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class OrderService implements PlaceOrderUseCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;

    @Override
    public Order placeOrder(PlaceOrderCommand command) {

        LOGGER.info("Executing PlaceOrderCommand command {}", command);

        Order build = Order.builder()
                .customerId(command.customerId())
                .size(command.size())
                .status(OrderStatus.RECEIVED)
                .toppings(command.toppings().stream().map(Topping::valueOf).collect(Collectors.toList()))
                .build();

        Order added = orderRepository.add(build);
        LOGGER.info("Added order {}", added.getPlacedAt());

        return added;
    }
}
