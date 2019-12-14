package io.costax.food4u.domain.model;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;

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
