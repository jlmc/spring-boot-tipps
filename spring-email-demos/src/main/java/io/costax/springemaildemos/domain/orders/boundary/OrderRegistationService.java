package io.costax.springemaildemos.domain.orders.boundary;

import io.costax.springemaildemos.domain.orders.control.OrderRepository;
import io.costax.springemaildemos.domain.orders.entity.Order;
import io.costax.springemaildemos.domain.orders.entity.OrderItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Set;

@Service
public class OrderRegistationService {

    private final OrderRepository repository;


    public OrderRegistationService(final OrderRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Order create(Order request) {
        // do not use the request...

        final Order newOrder = Order.createOrder(request.getClient());

        final Set<OrderItem> items = request.getItems();
        for (final OrderItem item : items) {
            newOrder.addItem(item.getProduct(), item.getUnitValue(), item.getQty());
        }

        repository.saveAndFlush(newOrder);

        // do other shits ...

        newOrder.confirm(OffsetDateTime.now());

        // we need to execute the save method
        repository.save(newOrder);

        return newOrder;
    }

}
