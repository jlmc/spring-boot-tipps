package io.costax.food4u.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@JsonRootName("cook")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Cooker {


    @EqualsAndHashCode.Include

    @NotNull(groups = ValidationGroups.RestaurantRegistration.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @JsonProperty("title")
    @Column(nullable = false)
    private String name;

    @JsonIgnore
    @Version
    private int version;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getVersion() {
        return version;
    }
}
