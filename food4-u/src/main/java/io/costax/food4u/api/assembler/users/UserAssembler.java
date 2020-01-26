package io.costax.food4u.api.assembler.users;

import io.costax.food4u.api.assembler.Disassembler;
import io.costax.food4u.api.model.users.input.UserInputRepresentation;
import io.costax.food4u.domain.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserAssembler implements
        Disassembler<User, UserInputRepresentation> {

    @Autowired
    ModelMapper modelMapper;

    @Override
    public User toDomainObject(final UserInputRepresentation payload) {
        return modelMapper.map(payload, User.class);
    }
}
