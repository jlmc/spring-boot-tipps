package io.costax.springemaildemos.domain.orders.entity;

import lombok.Data;
import org.springframework.data.domain.AbstractAggregateRoot;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
@Table(name = "t_order")
public class Order extends AbstractAggregateRoot<Order> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "t_order_id", nullable = false, updatable = false)
    private Set<OrderItem> items = new HashSet<>();

    private String client;

    private BigDecimal total = BigDecimal.ZERO;

    private OffsetDateTime confirmedAt;

    @Deprecated
    public Order() {
    }

    private Order(String client) {
        this.client = client;
    }

    public static Order createOrder(String client) {
        return new Order(client);
    }

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

    public Order addItem(String product, BigDecimal unitValue, Integer qty) {
        final OrderItem oi = OrderItem.of(product, unitValue, qty);
        this.items.add(oi);
        this.total = this.total.add(oi.getValue());
        return this;
    }
}
