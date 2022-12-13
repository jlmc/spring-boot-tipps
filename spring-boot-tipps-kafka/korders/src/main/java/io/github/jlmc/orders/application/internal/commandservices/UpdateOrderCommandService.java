package io.github.jlmc.orders.application.internal.commandservices;

import io.github.jlmc.orders.application.internal.outboundservices.acl.OrderEventProducer;
import io.github.jlmc.orders.domain.model.aggregates.Order;
import io.github.jlmc.orders.domain.model.commands.UpdateOrderCommand;
import io.github.jlmc.orders.domain.model.exceptions.OrderNotFoundException;
import io.github.jlmc.orders.domain.model.valueobjects.OrderItem;
import io.github.jlmc.orders.infrastructure.repositories.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Service
@Transactional
public class UpdateOrderCommandService {

    private OrderRepository orderRepository;
    private ProductResolver productResolver;
    private OrderEventProducer orderEventProducer;

    public Order execute(UpdateOrderCommand command) {
        Order exestingOrder =
                orderRepository.findById(command.orderId())
                               .orElseThrow(OrderNotFoundException::new);

        List<OrderItem> orderItems =
                command.items()
                       .stream()
                       .map(item -> OrderItem.of(productResolver.productOf(item.id()), item.qty()))
                       .toList();

        Order updatedOrder = orderRepository.save(Order.builder()
                                                       .id(exestingOrder.getId())
                                                       .created(exestingOrder.getCreated())
                                                       .orderItems(orderItems)
                                                       .build());

        orderEventProducer.sendUpdateOrderEvent(updatedOrder);

        return updatedOrder;
    }
}
