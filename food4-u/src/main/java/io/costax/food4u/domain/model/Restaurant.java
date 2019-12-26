package io.costax.food4u.domain.model;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.costax.food4u.core.validation.TakeAwayTax;
import io.costax.food4u.domain.model.ValidationGroups.CookerId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.groups.ConvertGroup;
import javax.validation.groups.Default;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
//@ZeroValueIncludesDescription(numericPropertyName = "takeAwayTax", descriptionPropertyName = "name", subDescriptionValue = "(Fee Take Way)")
public class Restaurant {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    //@PositiveOrZero
    //@Multiple(number = 2)
    @TakeAwayTax
    @Column(name = "take_away_tax", nullable = false)
    private BigDecimal takeAwayTax = BigDecimal.ZERO;

    //@JsonIgnoreProperties(value = "title", allowGetters = true)
    @Valid
    @ConvertGroup(from = Default.class, to = CookerId.class)
    @NotNull
    @ManyToOne
    @JoinColumn(name = "cooker_id", nullable = false)
    private Cooker cooker;

    @Embedded
    private Address address;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, updatable = false)
    private OffsetDateTime updatedAt;

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

    @Version
    private int version;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void updateNameTaxsCookerAndAddressUsing(final Restaurant other) {
        this.setCooker(other.getCooker());
        this.setAddress(other.getAddress());
        this.setName(other.getName());
        this.setTakeAwayTax(other.getTakeAwayTax());
    }
}
