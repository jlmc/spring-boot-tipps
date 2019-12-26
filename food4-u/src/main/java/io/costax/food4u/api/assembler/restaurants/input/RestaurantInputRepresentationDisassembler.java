package io.costax.food4u.api.assembler.restaurants.input;

import io.costax.food4u.api.assembler.Disassembler;
import io.costax.food4u.api.model.restaurants.input.CookerInputRepresentation;
import io.costax.food4u.api.model.restaurants.input.RestaurantInputRepresentation;
import io.costax.food4u.domain.model.Address;
import io.costax.food4u.domain.model.Cooker;
import io.costax.food4u.domain.model.Restaurant;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RestaurantInputRepresentationDisassembler implements Disassembler<Restaurant, RestaurantInputRepresentation> {

    @Override
    public Restaurant toDomainObject(RestaurantInputRepresentation restaurantInput) {
        if (restaurantInput == null) return null;

        Restaurant restaurant = new Restaurant();
        restaurant.setName(restaurantInput.getName());
        restaurant.setTakeAwayTax(restaurantInput.getTakeAwayTax());


        Cooker cooker = Optional.of(restaurantInput)
                .map(RestaurantInputRepresentation::getCooker)
                .map(CookerInputRepresentation::getId)
                .map(cookerId -> {
                    Cooker c = new Cooker();
                    c.setId(cookerId);
                    return c;
                }).orElse(null);

        restaurant.setCooker(cooker);

        final Address address = Optional.of(restaurantInput)
                .map(RestaurantInputRepresentation::getAddress)
                .map(a -> Address.of(a.getStreet(), a.getCity(), a.getZipCode())).orElse(null);
        restaurant.setAddress(address);

        return restaurant;
    }

}
