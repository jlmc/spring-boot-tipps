package io.costax.food4u.api.assembler.groups;

import io.costax.food4u.api.assembler.Assembler;
import io.costax.food4u.api.model.groups.output.GroupOutputRepresentation;
import io.costax.food4u.domain.model.Group;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GroupAssembler implements
        Assembler<GroupOutputRepresentation, Group> {
    @Autowired
    ModelMapper modelMapper;

    @Override
    public GroupOutputRepresentation toRepresentation(final Group group) {
        return modelMapper.map(group, GroupOutputRepresentation.class);
    }
}
