package io.costax.food4u.api;

import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ApiLinks {

    public Link restaurantPaymentMethodsLink(Long restauranteId, String rel) {
        return linkTo(methodOn(RestaurantResources.class).getRestaurantPaymentMethods(restauranteId)).withRel(rel);
    }

    public Link restaurantActivationLink(Long restaurantId, String rel) {
        return linkTo(methodOn(RestaurantResources.class)
                .ativar(restaurantId)).withRel(rel);
    }

    public Link restaurantInactivationLink(Long restaurantId, String rel) {
        final Link link = linkTo(methodOn(RestaurantResources.class)
                .inactivate(restaurantId)).withRel(rel);
        return link;
    }
}
