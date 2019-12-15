package io.costax.food4u.domain.model;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)

public class Restaurant {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "take_away_tax", nullable = false)
    private BigDecimal takeAwayTax;

    @ManyToOne
    @JoinColumn(name = "cooker_id", nullable = false)
    private Cooker cooker;

    @Embedded
    private Address address;

    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "restaurant_payment_method",
            joinColumns = @JoinColumn(
                    name = "restaurant_id",
                    referencedColumnName = "id",
                    nullable = false,
                    updatable = false),
            inverseJoinColumns = @JoinColumn(
                    name = "payment_method_id",
                    referencedColumnName = "id",
                    nullable = false,
                    updatable = false
            )
    )
    private Set<PaymentMethod> paymentMethods = new HashSet<>();

    @JsonIgnore
    @Version
    private int version;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
