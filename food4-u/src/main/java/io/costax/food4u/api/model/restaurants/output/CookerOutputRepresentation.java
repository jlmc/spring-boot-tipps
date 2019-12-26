package io.costax.food4u.api.model.restaurants.output;

import io.costax.food4u.domain.model.Cooker;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CookerOutputRepresentation {

    private Long id;
    private String title;

    public static CookerOutputRepresentation of(final Cooker cooker) {
        if (cooker == null) return null;

        CookerOutputRepresentation representation = new CookerOutputRepresentation();
        representation.id = cooker.getId();
        representation.title = cooker.getName();
        return representation;
    }
}
