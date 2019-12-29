package io.costax.food4u.domain.exceptions;

import io.costax.food4u.domain.model.Product;

import java.io.Serializable;

import static java.lang.String.format;

public class RestaurantProductNotFoundException extends ResourceNotFoundException {

    private RestaurantProductNotFoundException(final Serializable restaurantIdentifier, final Serializable productIdentifier) {
        super(Product.class,
                productIdentifier,
                format("Product with the identifier [%s] no found in the Restaurant with the identifier [%s]",
                        productIdentifier, restaurantIdentifier));
    }

    public static RestaurantProductNotFoundException of(final Serializable restaurantIdentifier,
                                                        final Serializable productIdentifier) {
        return new RestaurantProductNotFoundException(restaurantIdentifier, productIdentifier);
    }
}
