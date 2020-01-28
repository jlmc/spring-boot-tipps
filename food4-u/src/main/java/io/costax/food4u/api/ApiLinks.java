package io.costax.food4u.api;

import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ApiLinks {

    public Link restaurantPaymentMethodsLink(Long restaurantId, String rel) {
        return linkTo(methodOn(RestaurantResources.class)
                .getRestaurantPaymentMethods(restaurantId))
                .withRel(rel);
    }

    public Link restaurantActivationLink(Long restaurantId, String rel) {
        return linkTo(methodOn(RestaurantResources.class)
                .ativar(restaurantId))
                .withRel(rel);
    }

    public Link restaurantInactivationLink(Long restaurantId, String rel) {
        return linkTo(methodOn(RestaurantResources.class)
                .inactivate(restaurantId))
                .withRel(rel);
    }

    public Link requestsLink(final String rel) {
        return linkTo(RequestResources.class).withRel(rel);
    }

    public Link requestsConfirmationLink(final String code, final String rel) {
        return linkTo(methodOn(RequestResources.class).confirm(code)).withRel(rel);
    }

    public Link requestsCancelLink(final String code, final String rel) {
        return linkTo(methodOn(RequestResources.class).cancel(code)).withRel(rel);
    }

    public Link requestsDeliveryLink(final String code, final String rel) {
        return linkTo(methodOn(RequestResources.class).delivery(code)).withRel(rel);
    }
}
