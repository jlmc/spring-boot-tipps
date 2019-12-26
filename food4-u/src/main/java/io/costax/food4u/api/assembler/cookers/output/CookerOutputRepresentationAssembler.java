package io.costax.food4u.api.assembler.cookers.output;

import io.costax.food4u.api.assembler.Assembler;
import io.costax.food4u.api.model.cookers.output.CookerOutputRepresentation;
import io.costax.food4u.domain.model.Cooker;
import org.springframework.stereotype.Component;

@Component
public class CookerOutputRepresentationAssembler implements Assembler<CookerOutputRepresentation, Cooker> {

    @Override
    public CookerOutputRepresentation toRepresentation(Cooker cooker) {
        return CookerOutputRepresentation.of(cooker);
    }
}
