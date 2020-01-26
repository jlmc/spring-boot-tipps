package io.costax.food4u.api;

import io.costax.food4u.api.assembler.cookers.input.CookerInputRepresentationDisassembler;
import io.costax.food4u.api.assembler.cookers.output.CookerOutputRepresentationAssembler;
import io.costax.food4u.api.model.CookersXmlWrapper;
import io.costax.food4u.api.model.cookers.input.CookerInputRepresentation;
import io.costax.food4u.api.model.cookers.output.CookerOutputRepresentation;
import io.costax.food4u.api.openapi.controllers.CookerResourcesOpenApi;
import io.costax.food4u.domain.exceptions.CookerNotFoundException;
import io.costax.food4u.domain.model.Cooker;
import io.costax.food4u.domain.repository.CookerRepository;
import io.costax.food4u.domain.services.CookerRegistrationService;
import io.swagger.annotations.ApiOperation;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

/**
 * The {@link @RestController} annotation is a wrapper of two important annotations
 * {@link Controller} and {@link ResponseBody}
 */
@RestController
@RequestMapping(
        value = "/cookers",
        produces = {
                MediaType.APPLICATION_JSON_VALUE,
                MediaType.APPLICATION_XML_VALUE
        })
public class CookerResources implements CookerResourcesOpenApi {

    private final CookerRepository repository;
    private final CookerRegistrationService cookerRegistrationService;
    private final CookerOutputRepresentationAssembler assembler;
    private final CookerInputRepresentationDisassembler disassembler;

    public CookerResources(final CookerRepository repository,
                           final CookerRegistrationService cookerRegistrationService,
                           final CookerOutputRepresentationAssembler assembler,
                           final CookerInputRepresentationDisassembler disassembler) {
        this.repository = repository;
        this.cookerRegistrationService = cookerRegistrationService;
        this.assembler = assembler;
        this.disassembler = disassembler;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public CollectionModel<CookerOutputRepresentation> list() {
        return assembler.toCollectionModel(repository.findAll());

        /*
        final List<CookerOutputRepresentation> list = repository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        list.forEach(m -> {
            m.add(linkTo(methodOn(CookerResources.class).getById(m.getId()))
                    .withSelfRel());
            m.add(linkTo(methodOn(CookerResources.class).list())
                    .withRel(IanaLinkRelations.COLLECTION));
        });


        final CollectionModel<CookerOutputRepresentation> collectionModel = new CollectionModel<>(list);

        collectionModel.add(linkTo(CookerResources.class).withSelfRel());

        return collectionModel;

         */
    }

    @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
    public CookersXmlWrapper listXml() {
        return new CookersXmlWrapper(repository.findAll());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{Id}")
    public CookerOutputRepresentation getById(@PathVariable("Id") Long cookerId) {

        final CookerOutputRepresentation cookerOutput = repository.findById(cookerId)
                .map(assembler::toModel)
                .orElseThrow(() -> CookerNotFoundException.of(cookerId));

//        cookerOutput.add(linkTo(
//                methodOn(CookerResources.class)
//                        .getById(cookerOutput.getId())).withSelfRel());
//        cookerOutput.add(linkTo(
//                methodOn(CookerResources.class)
//                        .list()).withRel(IanaLinkRelations.COLLECTION));

        return cookerOutput;
    }

    @ApiOperation("Create new cooker")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CookerOutputRepresentation> add(
            @RequestBody @Valid CookerInputRepresentation payload,
            UriComponentsBuilder b) {
        Cooker saved = cookerRegistrationService.add(disassembler.toDomainObject(payload));

        //UriComponents uriComponents = b.path("/cookers/{id}").buildAndExpand(saved.getId());
        //final URI uri = uriComponents.toUri();

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();

        return ResponseEntity.created(location).body(assembler.toModel(saved));
    }

    @PutMapping("/{Id}")
    public ResponseEntity<CookerOutputRepresentation> update(
            @PathVariable("Id") Long cookerId,
            @RequestBody CookerInputRepresentation payload) {
        final Cooker cooker = disassembler.toDomainObject(payload);

        //Cooker cookerCurrent = cookerRegistrationService.update(cookerId, cooker);
        Cooker cookerCurrent = this.cookerRegistrationService.update(cookerId, cooker);
        return ResponseEntity.ok(assembler.toModel(cookerCurrent));
    }

    @DeleteMapping("/{Id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> remover(@PathVariable("Id") Long cookerId) {
        this.cookerRegistrationService.remove(cookerId);
        return ResponseEntity.noContent().build();
    }

    /*@ResponseBody
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ResponseEntity<?> resourceNotFoundHandler(ResourceNotFoundException e) {
        return ResponseEntity.notFound().header("X-reason", e.getMessage()).build();
    }

    @ResponseBody
    @ExceptionHandler(ResourceInUseException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ResponseEntity<?> resourceInUseHandler(ResourceInUseException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).header("X-reason", e.getMessage()).build();
    }*/

}
