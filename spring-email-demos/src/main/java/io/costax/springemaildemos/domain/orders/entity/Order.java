package io.costax.springemaildemos.domain.orders.entity;

import lombok.Data;
import org.springframework.data.domain.AbstractAggregateRoot;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "t_order")
@Data
public class Order extends AbstractAggregateRoot<Order> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String productId;

    private Integer qty;

    private OffsetDateTime confirmedAt;

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

    public void confirm(OffsetDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;

        // do some business

        registerEvent(new OrderConfirmedEvent(this));
    }
}
