package io.github.jlmc.poc.domain.orders.boundary;

import io.github.jlmc.poc.domain.orders.commands.CreateOrderCommand;
import io.github.jlmc.poc.domain.orders.entities.Order;
import io.github.jlmc.poc.domain.orders.entities.OrderId;
import io.github.jlmc.poc.domain.orders.entities.Product;
import io.github.jlmc.poc.domain.orders.ports.incoming.CreateOrderReservation;
import io.github.jlmc.poc.domain.orders.ports.outgoing.OrderIdCreator;
import io.github.jlmc.poc.domain.orders.ports.outgoing.OrderRepository;
import io.github.jlmc.poc.domain.orders.ports.outgoing.ProductProvider;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Validated
@Service
public class OrderCreatorService implements CreateOrderReservation {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderCreatorService.class);

    @Autowired
    ProductProvider productProvider;

    @Autowired
    OrderIdCreator orderIdCreator;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    Clock clock;

    @Override
    public OrderId createOrder(@Valid CreateOrderCommand createOrderCommand) {
        LOGGER.info("Creating new order");

        Map<String, Product> productMap = getProductMapById(createOrderCommand);

        Order order = createOrder(createOrderCommand, productMap);

        orderRepository.save(order);

        LOGGER.info("Order created [{}]", order);

        return order.getId();
    }

    private Order createOrder(CreateOrderCommand createOrderCommand, Map<String, Product> productMap) {
        var builder = Order.builder()
                .id(orderIdCreator.generateOrderId());

        for (CreateOrderCommand.Item item : createOrderCommand.items()) {
            Product product = productMap.get(item.productId());
            Integer quantity = item.quantity();

            builder.addItem(product, quantity);
        }

        return builder.build();
    }

    private Map<String, Product> getProductMapById(CreateOrderCommand createOrderCommand) {
        LOGGER.debug("Getting product map");
        Instant startup = Instant.now(clock);


        Map<String, Product> productMap = new HashMap<>();

        for (CreateOrderCommand.Item item : createOrderCommand.items()) {
            if (productMap.containsKey(item.productId())) {
                continue;
            }

            Integer productId = Integer.valueOf(item.productId());
            Product product = productProvider.getProduct(productId);

            productMap.put(item.productId(), product);
        }

        Instant ends = Instant.now(clock);

        Duration duration = Duration.between(startup, ends);

        LOGGER.debug("Got product map successfully in [{}] in mills [{}] ", duration, duration.toMillis());

        return productMap;
    }
}
