package io.costax.food4u.domain.exceptions;

import java.io.Serializable;

public class BadRestaurantIdException extends BusinessException {

    private BadRestaurantIdException(final Serializable restaurantId) {
        super(String.format("The System do not contain any restaurant with the identifier '%d'", restaurantId));
    }

    public static BadRestaurantIdException of(final Serializable restaurantId) {
        return new BadRestaurantIdException(restaurantId);
    }
}
