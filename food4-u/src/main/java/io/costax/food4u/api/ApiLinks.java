package io.costax.food4u.api;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.TemplateVariable;
import org.springframework.hateoas.TemplateVariables;
import org.springframework.hateoas.UriTemplate;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ApiLinks {

    public Link cookerByIdLink(Long cookerId, String rel) {
        return linkTo(methodOn(CookerResources.class)
                .getById(cookerId))
                .withRel(rel);
    }


    public Link restaurantProductsLink(Long restaurantId, String rel) {
        // RestaurantProductsSubResources
        return linkTo(methodOn(RestaurantProductsSubResources.class)
                .list(restaurantId, null))
                .withRel(rel);
    }

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

    /**
     * Build the template link to requests endpoint with all the query parameters.
     */
    public Link requestsLinks(final String rel) {

        // Template Links Example
        TemplateVariables pageVariables = new TemplateVariables(
                new TemplateVariable("page", TemplateVariable.VariableType.REQUEST_PARAM),
                new TemplateVariable("size", TemplateVariable.VariableType.REQUEST_PARAM),
                new TemplateVariable("sort", TemplateVariable.VariableType.REQUEST_PARAM)
        );

        TemplateVariables searchVariables = new TemplateVariables(
                new TemplateVariable("createdAt", TemplateVariable.VariableType.REQUEST_PARAM),
                new TemplateVariable("restaurantActive", TemplateVariable.VariableType.REQUEST_PARAM),
                new TemplateVariable("clientId", TemplateVariable.VariableType.REQUEST_PARAM)
        );

        String requestsUrl = linkTo(RequestResources.class).toUri().toString();

        return new Link(UriTemplate.of(requestsUrl, pageVariables.concat(searchVariables)), rel);
    }
}
