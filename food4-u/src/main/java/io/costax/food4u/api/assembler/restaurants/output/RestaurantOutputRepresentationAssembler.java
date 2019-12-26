package io.costax.food4u.api.assembler.restaurants.output;

import io.costax.food4u.api.assembler.Assembler;
import io.costax.food4u.api.model.restaurants.output.RestaurantOutputRepresentation;
import io.costax.food4u.domain.model.Restaurant;
import org.springframework.stereotype.Component;

@Component
public class RestaurantOutputRepresentationAssembler implements Assembler<RestaurantOutputRepresentation, Restaurant> {

    @Override
    public RestaurantOutputRepresentation toRepresentation(Restaurant restaurant) {
        return RestaurantOutputRepresentation.of(restaurant);
    }

}
