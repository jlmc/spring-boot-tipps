package io.costax.food4u.domain.exceptions;

import io.costax.food4u.domain.model.Restaurant;

import java.io.Serializable;

public class RestaurantNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    private RestaurantNotFoundException(final Serializable identifier) {
        super(Restaurant.class, identifier);
    }


    public static RestaurantNotFoundException of(Long restaurantId) {
        return new RestaurantNotFoundException(restaurantId);
    }
}
