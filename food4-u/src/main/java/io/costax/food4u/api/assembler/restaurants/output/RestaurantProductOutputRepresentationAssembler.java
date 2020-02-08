package io.costax.food4u.api.assembler.restaurants.output;

import io.costax.food4u.api.ApiLinks;
import io.costax.food4u.api.RestaurantProductsSubResources;
import io.costax.food4u.api.model.restaurants.output.ProductOutputRepresentation;
import io.costax.food4u.domain.model.Product;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class RestaurantProductOutputRepresentationAssembler
        extends RepresentationModelAssemblerSupport<Product, ProductOutputRepresentation> {

    @Autowired
    ApiLinks apiLinks;

    @Autowired
    ModelMapper modelMapper;

    /**
     * Creates a new {@link RepresentationModelAssemblerSupport} using the given controller class and resource type.
     */
    public RestaurantProductOutputRepresentationAssembler() {
        super(RestaurantProductsSubResources.class, ProductOutputRepresentation.class);
    }

    @Override
    public ProductOutputRepresentation toModel(final Product entity) {
        final ProductOutputRepresentation model = modelMapper.map(entity, ProductOutputRepresentation.class);

        addLinks(entity.getRestaurantId(), entity.getId(), model);

        return model;
    }


    private void addLinks(final Long restaurantId, final Long productId, final ProductOutputRepresentation model) {
        model.add(
                apiLinks.restaurantProductLink(restaurantId, productId, IanaLinkRelations.SELF.value()),
                apiLinks.restaurantProductPhotoLink(restaurantId, productId, "photo"));
    }
}
