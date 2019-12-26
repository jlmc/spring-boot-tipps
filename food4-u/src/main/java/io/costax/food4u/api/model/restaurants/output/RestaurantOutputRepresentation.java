package io.costax.food4u.api.model.restaurants.output;

import io.costax.food4u.api.model.cookers.output.CookerOutputRepresentation;
import io.costax.food4u.domain.model.Restaurant;
import lombok.Data;

import java.math.BigDecimal;

@Data
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestaurantOutputRepresentation {

    private Long id;
    private String name;
    private BigDecimal takeAwayTax = BigDecimal.ZERO;
    private CookerOutputRepresentation cooker;
    private AddressOutputRepresentation address;

    public static RestaurantOutputRepresentation of(final Restaurant restaurant) {
        if (restaurant == null) return null;

        RestaurantOutputRepresentation representation = new RestaurantOutputRepresentation();
        representation.id = restaurant.getId();
        representation.name = restaurant.getName();
        representation.takeAwayTax = restaurant.getTakeAwayTax();
        representation.cooker = CookerOutputRepresentation.of(restaurant.getCooker());
        representation.address = AddressOutputRepresentation.of(restaurant.getAddress());

        return representation;
    }
}
