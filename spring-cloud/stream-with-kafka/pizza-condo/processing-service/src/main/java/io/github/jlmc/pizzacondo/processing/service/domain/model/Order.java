package io.github.jlmc.pizzacondo.processing.service.domain.model;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;

@Value
@Builder
public class Order {
    String id;
    Instant placedAt;
    String customerId;
    int size;
    List<String> toppings;
}
