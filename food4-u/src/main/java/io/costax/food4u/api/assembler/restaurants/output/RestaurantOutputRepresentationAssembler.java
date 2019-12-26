package io.costax.food4u.api.assembler.restaurants.output;

import io.costax.food4u.api.assembler.Assembler;
import io.costax.food4u.api.model.restaurants.output.RestaurantOutputRepresentation;
import io.costax.food4u.domain.model.Restaurant;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RestaurantOutputRepresentationAssembler implements Assembler<RestaurantOutputRepresentation, Restaurant> {


    @Autowired
    ModelMapper modelMapper;

    @Override
    public RestaurantOutputRepresentation toRepresentation(Restaurant restaurant) {
        return modelMapper.map(restaurant, RestaurantOutputRepresentation.class);
    }

}
