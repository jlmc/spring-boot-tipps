package io.costax.food4u.api.assembler.cookers.output;

import io.costax.food4u.api.assembler.Assembler;
import io.costax.food4u.api.model.cookers.output.CookerOutputRepresentation;
import io.costax.food4u.domain.model.Cooker;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CookerOutputRepresentationAssembler implements Assembler<CookerOutputRepresentation, Cooker> {

    @Autowired
    ModelMapper modelMapper;

    @Override
    public CookerOutputRepresentation toRepresentation(Cooker cooker) {
        return modelMapper.map(cooker, CookerOutputRepresentation.class);
    }
}
