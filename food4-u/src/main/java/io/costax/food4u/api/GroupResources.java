package io.costax.food4u.api;

import io.costax.food4u.api.assembler.Assembler;
import io.costax.food4u.api.model.groups.output.GroupOutputRepresentation;
import io.costax.food4u.domain.model.Group;
import io.costax.food4u.domain.repository.GroupRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/groups")
public class GroupResources {

    private final GroupRepository groupRepository;
    private final Assembler<GroupOutputRepresentation, Group> assembler;
    public GroupResources(final GroupRepository groupRepository,
                          final Assembler<GroupOutputRepresentation, Group> assembler) {
        this.groupRepository = groupRepository;
        this.assembler = assembler;
    }

    @GetMapping
    public List<GroupOutputRepresentation> list() {
        return assembler.toListOfRepresentations(groupRepository.findAll());
    }
}
