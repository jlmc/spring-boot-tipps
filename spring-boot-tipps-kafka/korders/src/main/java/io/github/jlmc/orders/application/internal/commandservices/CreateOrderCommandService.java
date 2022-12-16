package io.github.jlmc.orders.application.internal.commandservices;

import io.github.jlmc.orders.application.internal.outboundservices.acl.OrderEventProducer;
import io.github.jlmc.orders.application.internal.outboundservices.acl.OrderIdGeneratorService;
import io.github.jlmc.orders.domain.model.aggregates.Order;
import io.github.jlmc.orders.domain.model.commands.CreateOrderCommand;
import io.github.jlmc.orders.domain.model.entities.Product;
import io.github.jlmc.orders.domain.model.valueobjects.OrderItem;
import io.github.jlmc.orders.infrastructure.repositories.OrderRepository;
import io.github.jlmc.orders.infrastructure.repositories.ProductRepository;
import io.github.jlmc.orders.shareddomain.events.OrderEvent;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@AllArgsConstructor

@Service
@Transactional
public class CreateOrderCommandService {
    private OrderRepository repository;
    private OrderIdGeneratorService orderIdGeneratorService;
    private ProductResolver productResolver;
    private OrderEventProducer orderEventProducer;

    public Order execute(CreateOrderCommand command) {
        String generatedId = orderIdGeneratorService.generate();

        List<OrderItem> orderItems =
                command.items()
                       .stream()
                       .map(item -> OrderItem.of(productResolver.productOf(item.id()), item.qty()))
                       .toList();

        Order newOrder = repository.save(Order.builder()
                                              .id(generatedId)
                                              .created(Instant.now())
                                              .orderItems(orderItems)
                                              .build());

        this.orderEventProducer.sendCreateOrderEvent(newOrder);

        return newOrder;
    }

}
