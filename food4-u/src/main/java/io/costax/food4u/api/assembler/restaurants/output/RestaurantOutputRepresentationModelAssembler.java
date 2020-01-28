package io.costax.food4u.api.assembler.restaurants.output;

import io.costax.food4u.api.ApiLinks;
import io.costax.food4u.api.RestaurantResources;
import io.costax.food4u.api.model.restaurants.output.RestaurantOutputRepresentation;
import io.costax.food4u.domain.model.Restaurant;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class RestaurantOutputRepresentationModelAssembler
    extends RepresentationModelAssemblerSupport<Restaurant, RestaurantOutputRepresentation> {

    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ApiLinks apiLinks;

    public RestaurantOutputRepresentationModelAssembler() {
        super(RestaurantResources.class, RestaurantOutputRepresentation.class);
    }

    @Override
    public RestaurantOutputRepresentation toModel(final Restaurant entity) {
        final RestaurantOutputRepresentation model = createModelWithId(entity.getId(), entity);

        modelMapper.map(entity, model);

        model.add(linkTo(RestaurantResources.class).withRel(IanaLinkRelations.COLLECTION));
        model.add(apiLinks.restaurantPaymentMethodsLink(entity.getId(), "payment-methods"));

        if (!entity.isActive()) {
            model.add(apiLinks.restaurantActivationLink(entity.getId(), "ativar"));
        } else {
            model.add(apiLinks.restaurantInactivationLink(entity.getId(), "inactivate"));
        }

        return model;
    }
}
