package io.costax.food4u.api.assembler.cookers.output;

import io.costax.food4u.api.CookerResources;
import io.costax.food4u.api.model.cookers.output.CookerOutputRepresentation;
import io.costax.food4u.domain.model.Cooker;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CookerOutputRepresentationAssembler
        extends RepresentationModelAssemblerSupport<Cooker, CookerOutputRepresentation> {

    @Autowired
    ModelMapper modelMapper;

    public CookerOutputRepresentationAssembler() {
        super(CookerResources.class, CookerOutputRepresentation.class);
    }

    @Override
    public CookerOutputRepresentation toModel(Cooker cooker) {
        final CookerOutputRepresentation representation = createModelWithId(cooker.getId(), cooker);
        modelMapper.map(cooker, representation);

        //final CookerOutputRepresentation representation = modelMapper.map(cooker, CookerOutputRepresentation.class);

        // the base representation class already add the self link
        //representation.add(linkTo(methodOn(CookerResources.class).getById(representation.getId())).withSelfRel());
        representation.add(linkTo(methodOn(CookerResources.class).list())
                .withRel(IanaLinkRelations.COLLECTION));

        return representation;
    }
}
