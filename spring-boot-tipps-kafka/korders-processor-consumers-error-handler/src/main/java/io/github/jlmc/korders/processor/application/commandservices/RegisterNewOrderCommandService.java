package io.github.jlmc.korders.processor.application.commandservices;

import io.github.jlmc.korders.processor.domain.model.aggregates.Order;
import io.github.jlmc.korders.processor.domain.model.commands.RegisterNewOrderCommand;
import io.github.jlmc.korders.processor.domain.model.entities.OrderItem;
import io.github.jlmc.korders.processor.domain.model.valueobjects.Product;
import io.github.jlmc.korders.processor.domain.model.exceptions.IllegalProductException;
import io.github.jlmc.korders.processor.infrastruture.repositories.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class RegisterNewOrderCommandService {

    private OrderRepository orderRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE, readOnly = false)
    public Optional<Order> execute(RegisterNewOrderCommand command) {
        String orderId = command.orderId();

        if (orderRepository.existsById(orderId)) {
            return update(command);
        } else {
            return create(command);
        }
    }

    private Optional<Order> create(RegisterNewOrderCommand command) {
        Order newOrder = Order.builder()
                              .id(command.orderId())
                              .orderCreated(command.created())
                              .status(Order.Status.NEW)
                              .build();

        itemsOf(command).forEach(newOrder::addItem);

        return Optional.of(orderRepository.saveAndFlush(newOrder));
    }

    private Optional<Order> update(RegisterNewOrderCommand command) {
        Optional<Order> orderOptional = orderRepository.findOrderByIdFetchItems(command.orderId());
        if (orderOptional.isEmpty()) return Optional.empty();

        Order order = orderOptional.orElseThrow();

        order.replaceItems(itemsOf(command));

        return Optional.of(orderRepository.saveAndFlush(order));
    }

    private List<OrderItem> itemsOf(RegisterNewOrderCommand command) {
        return command
                .items()
                .stream()
                .map(p -> getProductDetailsItem(p.getFirst(), p.getSecond())).map(details ->
                        OrderItem.builder()
                                 .unitPrice(details.unitPrice())
                                 .tax(details.tax())
                                 .qty(details.qty())
                                 .product(Product.of(details.id(), details.name()))
                                 .build()
                )
                .toList();
    }

    private ProductDetailsItem getProductDetailsItem(String productId, Integer qty) {
        if (productId == null || "UNKNOWN".equalsIgnoreCase(productId)) {
            throw new IllegalArgumentException("invalid product id");
        }

        if ("9999_9999".equals(productId)) {
            throw new IllegalProductException("Product <" + productId + "> is not supported");
        }

        return new ProductDetailsItem(productId, BigDecimal.TEN, new BigDecimal(23), "Fake product", qty);
    }

    //@formatter:off
    record ProductDetailsItem(String id, BigDecimal unitPrice, BigDecimal tax, String name, Integer qty) { }
    //@formatter:on
}
