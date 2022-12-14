package io.costax.food4u.domain.services;

import io.costax.food4u.domain.exceptions.BadRestaurantIdException;
import io.costax.food4u.domain.exceptions.CookerNotFoundException;
import io.costax.food4u.domain.exceptions.ResourceNotFoundException;
import io.costax.food4u.domain.exceptions.RestaurantNotFoundException;
import io.costax.food4u.domain.model.Cooker;
import io.costax.food4u.domain.model.PaymentMethod;
import io.costax.food4u.domain.model.Restaurant;
import io.costax.food4u.domain.repository.CookerRepository;
import io.costax.food4u.domain.repository.PaymentMethodRepository;
import io.costax.food4u.domain.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Service
@Transactional
public class RestaurantRegistrationService {

    private final CookerRepository cookerRepository;
    private final RestaurantRepository restaurantRepository;
    private final PaymentMethodRepository paymentMethodRepository;

    public RestaurantRegistrationService(final CookerRepository cookerRepository,
                                         final RestaurantRepository restaurantRepository,
                                         final PaymentMethodRepository paymentMethodRepository) {
        this.cookerRepository = cookerRepository;
        this.restaurantRepository = restaurantRepository;
        this.paymentMethodRepository = paymentMethodRepository;
    }

    public Restaurant add(final Restaurant restaurant) {
        final Cooker cooker = verifyAndGetIfExistsCooker(restaurant);

        restaurant.setCooker(cooker);

        restaurantRepository.save(restaurant);
        restaurantRepository.flush();

        return restaurant;
    }

    private Cooker verifyAndGetIfExistsCooker(final Restaurant restaurant) {
        var cookerId = Optional.ofNullable(restaurant.getCooker())
                .map(Cooker::getId)
                .orElse(null);

        return Optional.ofNullable(restaurant.getCooker())
                .map(Cooker::getId)
                .flatMap(cookerRepository::findById)
                .orElseThrow(() -> CookerNotFoundException.of(cookerId));
    }

    public Restaurant update(final Long restaurantId, final Restaurant restaurant) {
        Restaurant current = getRestaurantOrNotFoundException(restaurantId);

        final Cooker cooker = verifyAndGetIfExistsCooker(restaurant);
        restaurant.setCooker(cooker);

        // NOTE: 26/12/2019 - the method BeanUtils.copyProperties make the code fragile, because when we need to add a new property
        //  we must to remember that when we add a new property we have to check this method and see if the property should be ignored
        //BeanUtils.copyProperties(restaurant, current, "id", "paymentMethods", "createdAt", "updatedAt", "version");

        // FIXME: 26/12/2019 - using a dedicated method also make the code fragile, because when we need to add a new property.
        //  For the same reasons as the BeanUtils method
        current.updateNameTaxsCookerAndAddressUsing(restaurant);

        restaurantRepository.flush();

        return current;
    }

    private Restaurant getRestaurantOrNotFoundException(final Long restaurantId) {
        return restaurantRepository
                .findById(restaurantId)
                .orElseThrow(() -> RestaurantNotFoundException.of(restaurantId));
    }

    @Transactional
    public void activate(Long restaurantId) {
        Restaurant restauranteAtual = getRestaurantOrNotFoundException(restaurantId);

        restauranteAtual.activate();
    }

    @Transactional
    public void inactivate(Long restaurantId) {
        Restaurant restauranteAtual = getRestaurantOrNotFoundException(restaurantId);

        restauranteAtual.inactivate();
    }

    @Transactional
    public void activate(Collection<Long> restaurantIds) {
        try {
            restaurantIds.forEach(this::activate);
        } catch (RestaurantNotFoundException e) {
            throw BadRestaurantIdException.of(e.getIdentifier());
        }
    }

    @Transactional
    public void addPaymentMethod(final Long restaurantId, final Long paymentMethodId) {
        final Restaurant restaurant = getRestaurantOrNotFoundException(restaurantId);
        final PaymentMethod paymentMethod = paymentMethodRepository
                .findById(paymentMethodId)
                .orElseThrow(() -> new ResourceNotFoundException(PaymentMethod.class, restaurantId));

        restaurant.addPaymentMethod(paymentMethod);

        restaurantRepository.flush();
    }

    @Transactional
    public void removePaymentMethod(final Long restaurantId, final Long paymentMethodId) {
        final Restaurant restaurant = getRestaurantOrNotFoundException(restaurantId);
        final PaymentMethod paymentMethod = paymentMethodRepository
                .findById(paymentMethodId)
                .orElseThrow(() -> new ResourceNotFoundException(PaymentMethod.class, restaurantId));

        restaurant.removePaymentMethod(paymentMethod);

        restaurantRepository.flush();
    }
}
