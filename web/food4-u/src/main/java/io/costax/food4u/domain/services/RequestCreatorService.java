package io.costax.food4u.domain.services;

import io.costax.food4u.domain.exceptions.BadResourceIdException;
import io.costax.food4u.domain.exceptions.BadRestaurantIdException;
import io.costax.food4u.domain.model.*;
import io.costax.food4u.domain.repository.RequestRepository;
import io.costax.food4u.domain.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
public class RequestCreatorService {

    private final RequestRepository requestRepository;
    private final RestaurantRepository restaurantRepository;

    public RequestCreatorService(final RequestRepository requestRepository, final RestaurantRepository restaurantRepository) {
        this.requestRepository = requestRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @Transactional
    public Request create(final User user, final Request in) {
        final Restaurant restaurant = restaurantRepository
                .findById(in.getRestaurant().getId())
                .orElseThrow(() -> BadRestaurantIdException.of(in.getRestaurant().getId()));

        final PaymentMethod paymentMethod = restaurant.getPaymentMethods()
                .stream()
                .filter(p -> Objects.equals(p, in.getPaymentMethod()))
                .findFirst()
                .orElseThrow(() -> BadResourceIdException.of(
                        PaymentMethod.class,
                        in.getPaymentMethod().getId(),
                        String.format("Invalid PaymentMethod in Restaurant %d", restaurant.getId())));

        Request request = Request.of(
                user,
                restaurant,
                paymentMethod,
                in.getDeliveryAddress());

        for (RequestItem ri : in.getItems()) {
            final Product product = Optional.ofNullable(ri.getProduct())
                    .map(Product::getId)
                    .flatMap(restaurant::getProduct)
                    .orElseThrow(() -> BadResourceIdException.of(
                            PaymentMethod.class,
                            in.getPaymentMethod().getId(),
                            String.format("Invalid Product in Restaurant %d", restaurant.getId())));

            request.addItem(product, ri.getQty(), ri.getObservations());
        }

        return requestRepository.saveAndFlush(request);
    }

}
