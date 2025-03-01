package io.github.jlmc.pizzacondo.om.service.domain.model;

import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
public class Order {
    private String id;
    private Instant placedAt;
    private String customerId;
    private int size;
    private List<Topping> toppings;
    private OrderStatus status;
}