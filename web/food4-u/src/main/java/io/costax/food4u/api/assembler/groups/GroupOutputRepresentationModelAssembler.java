package io.costax.food4u.api.assembler.groups;

import io.costax.food4u.api.GroupResources;
import io.costax.food4u.api.model.groups.output.GroupOutputRepresentation;
import io.costax.food4u.domain.model.Group;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class GroupOutputRepresentationModelAssembler extends RepresentationModelAssemblerSupport<Group, GroupOutputRepresentation> {

    @Autowired
    ModelMapper modelMapper;

    public GroupOutputRepresentationModelAssembler() {
        super(GroupResources.class, GroupOutputRepresentation.class);
    }

    @Override
    public GroupOutputRepresentation toModel(final Group entity) {
        final GroupOutputRepresentation model = createModelWithId(entity.getId(), entity);

        modelMapper.map(entity, model);

        model.add(linkTo(methodOn(GroupResources.class).list())
                .withRel(IanaLinkRelations.COLLECTION));

        return model;
    }
}
