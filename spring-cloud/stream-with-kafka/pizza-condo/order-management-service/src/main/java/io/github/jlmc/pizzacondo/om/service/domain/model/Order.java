package io.github.jlmc.pizzacondo.om.service.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

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

    public void cancel() {
        this.status = OrderStatus.CANCELLED;
    }

    public void dispatch() {
        this.status = OrderStatus.VALIDATED;
    }

    public List<String> toppingsAsString() {
        return toppings.stream().map(Topping::toString).collect(Collectors.toList());
    }

    public void inProcess() {
        this.status = OrderStatus.IN_PROGRESS;
    }

    public void inDelivery() {
        this.status = OrderStatus.IN_DELIVERY;
    }
}