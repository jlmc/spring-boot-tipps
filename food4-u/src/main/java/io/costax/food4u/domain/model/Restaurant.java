package io.costax.food4u.domain.model;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
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

    @NotBlank
    @Column(nullable = false)
    private String name;

    @PositiveOrZero
    @Column(name = "take_away_tax", nullable = false)
    private BigDecimal takeAwayTax = BigDecimal.ZERO;

    @Valid
    @NotNull
    @ManyToOne
    @JoinColumn(name = "cooker_id", nullable = false)
    private Cooker cooker;

    @Embedded
    private Address address;

    @JsonIgnore
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
    @JsonIgnore
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, updatable = false)
    private OffsetDateTime updatedAt;

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
