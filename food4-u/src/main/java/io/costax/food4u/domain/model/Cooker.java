package io.costax.food4u.domain.model;

import io.costax.food4u.domain.model.ValidationGroups.CookerId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * the @JsonRootName("cook") annotation make the root element name as cook when we get a single cooker
 */
//@JsonRootName("cook")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Cooker {

    @EqualsAndHashCode.Include
    @NotNull(groups = CookerId.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@JsonProperty("title")
    @NotBlank
    @Column(nullable = false)
    private String name;

    //@JsonIgnore
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
