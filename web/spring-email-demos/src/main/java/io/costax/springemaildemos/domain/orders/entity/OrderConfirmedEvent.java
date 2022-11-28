package io.costax.springemaildemos.domain.orders.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderConfirmedEvent {

    public Order order;
}
