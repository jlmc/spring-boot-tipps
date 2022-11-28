package io.github.jlmc.domain.model;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(name = "shipping_tax")
    private BigDecimal shippingTax = BigDecimal.ZERO;
    ;
}
