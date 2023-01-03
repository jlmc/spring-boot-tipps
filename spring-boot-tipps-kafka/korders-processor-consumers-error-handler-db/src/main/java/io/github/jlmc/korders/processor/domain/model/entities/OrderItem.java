package io.github.jlmc.korders.processor.domain.model.entities;

import io.github.jlmc.korders.processor.domain.model.valueobjects.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Min(1)
    @Column(name = "qty", nullable = false)
    private Integer qty = 1;

    @Min(0)
    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice = BigDecimal.ZERO;

    @Min(0)
    @Column(name = "tax", nullable = false)
    private BigDecimal tax = BigDecimal.ZERO;

    @Version
    private int version;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "product_id", nullable = false)),
            @AttributeOverride(name = "name", column = @Column(name = "product_name"))
    })
    private Product product;

    @Transient
    public BigDecimal getPrice() {
        return unitPrice.multiply(new BigDecimal(qty));
    }

    @Transient
    public BigDecimal getPriceWithTax() {
        BigDecimal txValueByUnit =
                unitPrice.multiply(tax.divide(new BigDecimal("100"), 2, RoundingMode.CEILING));
        BigDecimal unitPricePlusTx = unitPrice.add(txValueByUnit);

        return unitPricePlusTx.multiply(new BigDecimal(qty));
    }

}
