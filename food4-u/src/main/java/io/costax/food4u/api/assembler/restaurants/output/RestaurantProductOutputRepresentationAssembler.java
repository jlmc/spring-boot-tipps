package io.costax.food4u.api.assembler.restaurants.output;

import io.costax.food4u.api.assembler.Assembler;
import io.costax.food4u.api.model.restaurants.output.ProductOutputRepresentation;
import io.costax.food4u.domain.model.Product;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RestaurantProductOutputRepresentationAssembler implements
        Assembler<ProductOutputRepresentation, Product> {

    @Autowired
    ModelMapper modelMapper;

    @Override
    public ProductOutputRepresentation toRepresentation(final Product product) {
        return modelMapper.map(product, ProductOutputRepresentation.class);
    }
}
