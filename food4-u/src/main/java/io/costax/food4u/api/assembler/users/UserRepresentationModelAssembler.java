package io.costax.food4u.api.assembler.users;

import io.costax.food4u.api.UserResources;
import io.costax.food4u.api.model.users.output.UserOutputRepresentation;
import io.costax.food4u.domain.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserRepresentationModelAssembler extends RepresentationModelAssemblerSupport<User, UserOutputRepresentation> {

    @Autowired
    ModelMapper modelMapper;

    public UserRepresentationModelAssembler() {
        super(UserResources.class, UserOutputRepresentation.class);
    }

    @Override
    public UserOutputRepresentation toModel(final User user) {
        final UserOutputRepresentation model = createModelWithId(user.getId(), user);
        modelMapper.map(user, model);

        // the base representation class already add the self link
        model.add(linkTo(methodOn(UserResources.class).list())
                .withRel(IanaLinkRelations.COLLECTION));

        return model;
    }
}
