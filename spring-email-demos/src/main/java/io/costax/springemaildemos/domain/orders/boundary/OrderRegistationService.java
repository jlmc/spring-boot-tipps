package io.costax.springemaildemos.domain.orders.boundary;

import io.costax.springemaildemos.domain.orders.control.OrderRepository;
import io.costax.springemaildemos.domain.orders.entity.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class OrderRegistationService {

    private final OrderRepository repository;


    public OrderRegistationService(final OrderRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Order create(Order request) {
        final Order order = repository.saveAndFlush(request);

        // do other shits ...

        order.confirm(OffsetDateTime.now());

        // we need to execute the save method
        repository.save(order);

        return order;
    }

}
