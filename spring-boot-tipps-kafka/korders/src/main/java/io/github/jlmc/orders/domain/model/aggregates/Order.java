package io.github.jlmc.orders.domain.model.aggregates;


import io.github.jlmc.orders.domain.model.valueobjects.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {
    private String id;
    private Instant created;
    private List<OrderItem> orderItems = new ArrayList<>();
}
