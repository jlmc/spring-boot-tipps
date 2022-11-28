package io.costax.food4u.api;

import io.costax.food4u.api.assembler.groups.GroupOutputRepresentationModelAssembler;
import io.costax.food4u.api.model.groups.output.GroupOutputRepresentation;
import io.costax.food4u.api.openapi.controllers.GroupResourcesOpenApi;
import io.costax.food4u.domain.exceptions.ResourceNotFoundException;
import io.costax.food4u.domain.model.Group;
import io.costax.food4u.domain.repository.GroupRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/groups", produces = MediaType.APPLICATION_JSON_VALUE)
public class GroupResources implements GroupResourcesOpenApi {

    private final GroupRepository groupRepository;
    private final GroupOutputRepresentationModelAssembler assembler;

    public GroupResources(final GroupRepository groupRepository,
                          final GroupOutputRepresentationModelAssembler assembler) {
        this.groupRepository = groupRepository;
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<GroupOutputRepresentation> list() {
        return assembler.toCollectionModel(groupRepository.findAll());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{Id}")
    public GroupOutputRepresentation getById(@PathVariable("Id") Long id) {
        return groupRepository.findById(id)
                .map(assembler::toModel)
                .orElseThrow(() -> new ResourceNotFoundException(Group.class, id));
    }
}
