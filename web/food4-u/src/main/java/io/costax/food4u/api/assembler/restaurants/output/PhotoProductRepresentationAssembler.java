package io.costax.food4u.api.assembler.restaurants.output;

import io.costax.food4u.api.assembler.Assembler;
import io.costax.food4u.api.model.restaurants.output.PhotoProductRepresentation;
import io.costax.food4u.domain.model.Photo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PhotoProductRepresentationAssembler implements Assembler<PhotoProductRepresentation, Photo> {

    @Autowired
    ModelMapper modelMapper;

    @Override
    public PhotoProductRepresentation toRepresentation(final Photo photo) {
        return modelMapper.map(photo, PhotoProductRepresentation.class);
    }
}
