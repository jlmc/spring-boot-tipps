package io.github.jlmc.pizzacondo.om.service.application.services;

import io.github.jlmc.pizzacondo.om.service.application.port.input.CancelOrderUseCase;
import io.github.jlmc.pizzacondo.om.service.application.port.input.DispatchOrderUseCase;
import io.github.jlmc.pizzacondo.om.service.application.port.input.InProcessOrderUseCase;
import io.github.jlmc.pizzacondo.om.service.application.port.input.PlaceOrderUseCase;
import io.github.jlmc.pizzacondo.om.service.application.port.output.NotificationService;
import io.github.jlmc.pizzacondo.om.service.application.port.output.OrderRepository;
import io.github.jlmc.pizzacondo.om.service.domain.model.Order;
import io.github.jlmc.pizzacondo.om.service.domain.model.OrderStatus;
import io.github.jlmc.pizzacondo.om.service.domain.model.Topping;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Transactional
public class OrderService implements PlaceOrderUseCase, CancelOrderUseCase, DispatchOrderUseCase, InProcessOrderUseCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final NotificationService notificationService;

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

        notificationService.orderPlaced(added);

        return added;
    }

    @Override
    public void canselOrder(String orderId) {
        Order order = orderRepository.lookup(orderId).orElse(null);

        if (order == null) {
            LOGGER.warn("Order with id {} not found", orderId);
            return;
        }

        order.cancel();

        orderRepository.update(order);
    }

    @Override
    public void dispatchOrder(String orderId) {
        Order order = orderRepository.lookup(orderId).orElse(null);

        if (order == null) {
            LOGGER.warn("Order with id {} not found", orderId);
            return;
        }

        order.dispatch();

        orderRepository.update(order);

        notificationService.orderAccepted(order);
    }

    @Override
    public void inProcess(String orderId) {
        Order order = orderRepository.lookup(orderId).orElse(null);
        if (order != null) {
            order.inProcess();
            orderRepository.update(order);
        }
    }
}
