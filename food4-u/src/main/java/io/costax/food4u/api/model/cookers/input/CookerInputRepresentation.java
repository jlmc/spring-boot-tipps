package io.costax.food4u.api.model.cookers.input;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class CookerInputRepresentation {
    //@JsonProperty("title")
    @NotBlank
    @Column(nullable = false)
    private String title;
}
