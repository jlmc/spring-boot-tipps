package io.costax.springemaildemos.domain.orders.entity;

import lombok.Getter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter
@Table(name = "t_order_item")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String product;
    private BigDecimal unitValue;
    private Integer qty;

    @Deprecated
    public OrderItem() {
    }

    private OrderItem(final String product, final BigDecimal unitValue, final Integer qty) {
        this.product = product;
        this.unitValue = unitValue;
        this.qty = qty;
    }

    protected static OrderItem of(final String product, final BigDecimal unitValue, final Integer qty) {
        return new OrderItem(product, unitValue, qty);
    }

    public BigDecimal getValue() {
        return unitValue.multiply(BigDecimal.valueOf(qty));
    }
}
