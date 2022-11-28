package io.costax.food4u.api;

import org.springframework.hateoas.*;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ApiLinks {

    public Link linkToCookers(String rel) {
        return linkTo(
                methodOn(CookerResources.class).list())
                .withRel(rel);
    }

    public Link linkToCookerById(Long cookerId, String rel) {
        return linkTo(methodOn(CookerResources.class)
                .getById(cookerId))
                .withRel(rel);
    }

    public Link linkToRestaurantProducts(Long restaurantId, String rel) {
        // RestaurantProductsSubResources
        return linkTo(methodOn(RestaurantProductsSubResources.class)
                .list(restaurantId, null))
                .withRel(rel);
    }

    public Link linkToRestaurantPaymentMethods(Long restaurantId, String rel) {
        return linkTo(methodOn(RestaurantResources.class)
                .getRestaurantPaymentMethods(restaurantId))
                .withRel(rel);
    }

    public Link linkToRestaurantActivation(Long restaurantId, String rel) {
        return linkTo(methodOn(RestaurantResources.class)
                .ativar(restaurantId))
                .withRel(rel);
    }

    public Link linkToRestaurantInactivation(Long restaurantId, String rel) {
        return linkTo(methodOn(RestaurantResources.class)
                .inactivate(restaurantId))
                .withRel(rel);
    }

    public Link linkToRequests(final String rel) {
        return linkTo(RequestResources.class).withRel(rel);
    }

    public Link linkToRequestsConfirmation(final String code, final String rel) {
        return linkTo(methodOn(RequestResources.class).confirm(code)).withRel(rel);
    }

    public Link requestsCancelLink(final String code, final String rel) {
        return linkTo(methodOn(RequestResources.class).cancel(code)).withRel(rel);
    }

    public Link linkToRequestsDelivery(final String code, final String rel) {
        return linkTo(methodOn(RequestResources.class).delivery(code)).withRel(rel);
    }

    /**
     * Build the template link to requests endpoint with all the query parameters.
     */
    public Link linkToRequestsTemplateLinks(final String rel) {

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

        UriTemplate uriTemplate = UriTemplate.of(requestsUrl, pageVariables.concat(searchVariables));

        return Link.of(uriTemplate, LinkRelation.of(rel));
    }

    public Link linkToRestaurantProduct(Long restaurantId, Long productId, String rel) {
        return linkTo(
                methodOn(RestaurantProductsSubResources.class).findById(restaurantId, productId))
                .withRel(rel);
    }

    public Link linkToRestaurantProductPhoto(Long restaurantId, Long productId, String rel) {
        return linkTo(methodOn(RestaurantProductPhotoSubResource.class, restaurantId, productId)
                .getPhoto(restaurantId, productId))
                .withRel(rel);
    }

    public Link linkToGroups(final String rel) {
        return linkTo(methodOn(GroupResources.class).list()).withRel(rel);
    }

    public Link linkToPaymentMethods(final String rel) {
        return linkTo(methodOn(PaymentMethodResources.class).list(null)).withRel(rel);
    }

    public Link linkToRestaurants(final String rel) {
        return linkTo(methodOn(RestaurantResources.class).list()).withRel(rel);
    }

    public Link linkToUsers(final String rel) {
        return linkTo(methodOn(UserResources.class).list()).withRel(rel);
    }

    public Link linkToStatistics(String rel) {
        return linkTo(StatisticsResources.class).withRel(rel);
    }

    public Link linkToDailySalesStatistics(String rel) {

        TemplateVariables filtersVariables = new TemplateVariables(
                new TemplateVariable("restaurantActive", TemplateVariable.VariableType.REQUEST_PARAM),
                new TemplateVariable("createdAt", TemplateVariable.VariableType.REQUEST_PARAM));

        String pedidosUrl = linkTo(methodOn(StatisticsResources.class)
                .dailySalesStatisticsJson(null, null)).toUri().toString();

        return Link.of(UriTemplate.of(pedidosUrl, filtersVariables), rel);
    }
}
