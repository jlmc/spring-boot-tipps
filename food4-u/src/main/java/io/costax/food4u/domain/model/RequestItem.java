package io.costax.food4u.domain.model;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "request_item")
public class RequestItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private Integer qty = 1;
    private String observations;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false, updatable = false)
    private Product product;

    @Deprecated
    protected RequestItem() {}

    public RequestItem(final Product product, final Integer qty, final String observations) {
        this.observations = observations;
        this.product = product;
        this.unitPrice = product.getPrice();
        this.qty = qty;
        this.totalPrice = this.unitPrice.multiply(BigDecimal.valueOf(qty));
    }

    public static RequestItem of(final Product product, final Integer qty, final String observations) {
        return new RequestItem(product, qty, observations);
    }
}
