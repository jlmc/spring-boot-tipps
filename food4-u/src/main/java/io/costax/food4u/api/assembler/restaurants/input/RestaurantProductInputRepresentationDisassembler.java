package io.costax.food4u.api.assembler.restaurants.input;

import io.costax.food4u.api.assembler.Disassembler;
import io.costax.food4u.api.model.restaurants.input.ProductInputRepresentation;
import io.costax.food4u.domain.model.Product;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RestaurantProductInputRepresentationDisassembler implements
        Disassembler<Product, ProductInputRepresentation> {

    @Autowired
    ModelMapper modelMapper;

    @Override
    public Product toDomainObject(final ProductInputRepresentation payload) {
        return modelMapper.map(payload, Product.class);
    }
}
