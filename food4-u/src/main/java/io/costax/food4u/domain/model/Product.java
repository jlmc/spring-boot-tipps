package io.costax.food4u.domain.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.Objects;

@Data
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @PositiveOrZero
    @Column(nullable = false)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(nullable = false)
    private Boolean active = Boolean.TRUE;

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        final Product product = (Product) o;
        return getId() != null && Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(31);
    }

    protected Product updateData(final Product other) {

        this.name = other.getName();
        this.description = other.getDescription();
        this.price = other.getPrice();
        this.active = other.getActive();

        return this;
    }
}