package io.github.jlmc.pizzacondo.om.service.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder(toBuilder = true)
public class Order {
    @Setter
    private String id;
    @Setter
    private Instant placedAt;
    private String customerId;
    private int size;
    private List<Topping> toppings;
    private OrderStatus status;
}