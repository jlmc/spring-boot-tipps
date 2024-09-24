package io.github.jlmc.poc.domain.orders.ports.incoming;

import io.github.jlmc.poc.domain.orders.commands.CreateOrderCommand;
import io.github.jlmc.poc.domain.orders.entities.OrderId;
import jakarta.validation.Valid;

@Valid
public interface CreateOrderReservation {

    OrderId createOrder(@Valid CreateOrderCommand createOrderCommand);

}
