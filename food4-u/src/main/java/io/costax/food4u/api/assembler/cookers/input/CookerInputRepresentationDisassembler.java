package io.costax.food4u.api.assembler.cookers.input;

import io.costax.food4u.api.assembler.Disassembler;
import io.costax.food4u.api.model.cookers.input.CookerInputRepresentation;
import io.costax.food4u.domain.model.Cooker;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CookerInputRepresentationDisassembler implements Disassembler<Cooker, CookerInputRepresentation> {

    @Autowired
    ModelMapper modelMapper;

    @Override
    public Cooker toDomainObject(CookerInputRepresentation payload) {
        if (payload == null) return null;
        return modelMapper.map(payload, Cooker.class);
    }

}
