package io.costax.food4u.domain.services;

import io.costax.food4u.domain.exceptions.RestaurantProductNotFoundException;
import io.costax.food4u.domain.model.Product;
import io.costax.food4u.domain.model.Restaurant;
import io.costax.food4u.domain.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RestaurantProductsRegistrationService {

    private final RestaurantRepository restaurantRepository;

    public RestaurantProductsRegistrationService(final RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Transactional
    public Product add(final Long restaurantId, final Product product) {
        final Restaurant restaurant = restaurantRepository.getRestaurantOrNotFoundException(restaurantId);

        restaurant.addProduct(product);
        restaurantRepository.saveAndFlush(restaurant);

        return product;
    }

    @Transactional
    public Product update(final Long restaurantId, final Long productId, final Product product) {
        final Restaurant restaurant = restaurantRepository.getRestaurantOrNotFoundException(restaurantId);

        if (restaurant.notContainProduct(productId)) {
            throw RestaurantProductNotFoundException.of(restaurantId, productId);
        }

        Product p = restaurant.updateProductData(productId, product);

        restaurantRepository.flush();

        return p;
    }

    @Transactional
    public void delete(final Long restaurantId, final Long productId) {
        final Restaurant restaurant = restaurantRepository.getRestaurantOrNotFoundException(restaurantId);

        if (restaurant.notContainProduct(productId)) {
            throw RestaurantProductNotFoundException.of(restaurantId, productId);
        }

        restaurant.removeProduct(productId);

        restaurantRepository.flush();
    }
}
