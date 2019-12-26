package io.costax.food4u.api.assembler.cookers.input;

import io.costax.food4u.api.assembler.Disassembler;
import io.costax.food4u.api.model.cookers.input.CookerInputRepresentation;
import io.costax.food4u.domain.model.Cooker;
import org.springframework.stereotype.Component;

@Component
public class CookerInputRepresentationDisassembler implements Disassembler<Cooker, CookerInputRepresentation> {

    @Override
    public Cooker toDomainObject(CookerInputRepresentation cookerInputRepresentation) {
        if (cookerInputRepresentation == null) return null;
        Cooker restaurant = new Cooker();
        restaurant.setName(cookerInputRepresentation.getTitle());

        return restaurant;
    }

}
