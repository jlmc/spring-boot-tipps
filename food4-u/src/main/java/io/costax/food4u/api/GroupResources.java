package io.costax.food4u.api;

import io.costax.food4u.api.assembler.Assembler;
import io.costax.food4u.api.model.groups.output.GroupOutputRepresentation;
import io.costax.food4u.api.openapi.controllers.GroupResourcesOpenApi;
import io.costax.food4u.domain.model.Group;
import io.costax.food4u.domain.repository.GroupRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/groups", produces = MediaType.APPLICATION_JSON_VALUE)
public class GroupResources implements GroupResourcesOpenApi {

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
