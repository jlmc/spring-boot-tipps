package io.costax.food4u.domain.services;

import io.costax.food4u.domain.ResourceNotFoundException;
import io.costax.food4u.domain.model.Cooker;
import io.costax.food4u.domain.model.Restaurant;
import io.costax.food4u.domain.repository.CookerRepository;
import io.costax.food4u.domain.repository.RestaurantRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class RestaurantRegistrationService {

    private final CookerRepository cookerRepository;
    private final RestaurantRepository restaurantRepository;

    public RestaurantRegistrationService(final CookerRepository cookerRepository,
                                         final RestaurantRepository restaurantRepository) {
        this.cookerRepository = cookerRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public Restaurant add(final Restaurant restaurant) {
        final Cooker cooker = verifyAndGetIfExistsCooker(restaurant);

        restaurant.setCooker(cooker);

        restaurantRepository.save(restaurant);
        restaurantRepository.flush();

        return restaurant;
    }

    private Cooker verifyAndGetIfExistsCooker(final Restaurant restaurant) {
        return Optional.ofNullable(restaurant.getCooker())
                .map(Cooker::getId)
                .flatMap(cookerRepository::findById)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Cooker with id [%s] not found",
                                Optional.ofNullable(restaurant.getCooker())
                                        .map(Cooker::getId)
                                        .orElse(null))));
    }

    public Restaurant update(final Long restaurantId, final Restaurant restaurant) {
        Restaurant current = restaurantRepository
                .findById(restaurantId)
                .orElseThrow(() -> ResourceNotFoundException.of(Restaurant.class, restaurantId));

        final Cooker cooker = verifyAndGetIfExistsCooker(restaurant);
        restaurant.setCooker(cooker);

        BeanUtils.copyProperties(restaurant, current, "id", "paymentMethods", "createdAt", "updatedAt", "version");

        restaurantRepository.flush();

        return current;
    }
}
