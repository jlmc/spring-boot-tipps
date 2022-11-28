package io.costax.food4u.api.assembler.restaurants.input;

import io.costax.food4u.api.assembler.Disassembler;
import io.costax.food4u.api.model.restaurants.input.RestaurantInputRepresentation;
import io.costax.food4u.domain.model.Restaurant;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RestaurantInputRepresentationDisassembler implements Disassembler<Restaurant, RestaurantInputRepresentation> {

    @Autowired
    ModelMapper modelMapper;

    @Override
    public Restaurant toDomainObject(RestaurantInputRepresentation payload) {
        if (payload == null) return null;

        return modelMapper.map(payload, Restaurant.class);
    }

    public void copyProperties(RestaurantInputRepresentation payload, Restaurant domainEntity) {
        modelMapper.map(payload, domainEntity);
    }

}
