package io.costax.springemaildemos.domain.orders.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "t_order")
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String productId;

    private Integer qty;

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        final Order order = (Order) o;
        return getId() != null && Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
