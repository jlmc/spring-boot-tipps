package io.costax.food4u.api.model.restaurants.input;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CookerInputRepresentation {

    @NotNull
    private Long id;
}
