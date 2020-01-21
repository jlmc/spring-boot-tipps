package io.costax.food4u.api;

import io.costax.food4u.api.assembler.cookers.input.CookerInputRepresentationDisassembler;
import io.costax.food4u.api.assembler.cookers.output.CookerOutputRepresentationAssembler;
import io.costax.food4u.api.exceptionhandler.Problem;
import io.costax.food4u.api.model.CookersXmlWrapper;
import io.costax.food4u.api.model.cookers.input.CookerInputRepresentation;
import io.costax.food4u.api.model.cookers.output.CookerOutputRepresentation;
import io.costax.food4u.domain.exceptions.CookerNotFoundException;
import io.costax.food4u.domain.model.Cooker;
import io.costax.food4u.domain.repository.CookerRepository;
import io.costax.food4u.domain.services.CookerRegistrationService;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The {@link @RestController} annotation is a wrapper of two important annotations
 * {@link Controller} and {@link ResponseBody}
 */
@Api(tags = "Cookers")
@RestController
@RequestMapping(
        value = "/cookers",
        produces = {
                MediaType.APPLICATION_JSON_VALUE,
                MediaType.APPLICATION_XML_VALUE
        })
public class CookerResources {

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

    @ApiOperation("Get all cookers")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CookerOutputRepresentation> list() {
        return repository.findAll().stream()
                .map(assembler::toRepresentation)
                .collect(Collectors.toList());
    }

    //@ApiIgnore
    @ApiOperation("Get all cookers in xml")
    @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
    public CookersXmlWrapper listXml() {
        return new CookersXmlWrapper(repository.findAll());
    }

    @ApiOperation("Get cooker by id")
    @ApiResponses({
            @ApiResponse(code = 400, message = "Invalid Cooker ID", response = Problem.class),
            @ApiResponse(code = 404, message = "Cooker not found", response = Problem.class)
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{Id}")
    public CookerOutputRepresentation getById(
            @ApiParam(value = "Cooker ID", example = "1")
            @PathVariable("Id") Long cookerId) {
        return repository.findById(cookerId)
                .map(assembler::toRepresentation)
                .orElseThrow(() -> CookerNotFoundException.of(cookerId));
    }

    @ApiOperation("Create new cooker")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Cooker Created"),
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CookerOutputRepresentation> add(
            @ApiParam(name = "body", value = "Cooker Input Representation")
            @RequestBody @Valid CookerInputRepresentation payload,
                                                          UriComponentsBuilder b) {
        Cooker saved = cookerRegistrationService.add(disassembler.toDomainObject(payload));

        //UriComponents uriComponents = b.path("/cookers/{id}").buildAndExpand(saved.getId());
        //final URI uri = uriComponents.toUri();

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();

        return ResponseEntity.created(location).body(assembler.toRepresentation(saved));
    }

    @ApiOperation("Update cookers")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Updated Cooker"),
            @ApiResponse(code = 404, message = "Cooker not found", response = Problem.class)
    })
    @PutMapping("/{Id}")
    public ResponseEntity<CookerOutputRepresentation> update(
            @ApiParam(value = "Cooker ID", example = "1")
            @PathVariable("Id") Long cookerId,

            @RequestBody CookerInputRepresentation payload) {
        final Cooker cooker = disassembler.toDomainObject(payload);

        //Cooker cookerCurrent = cookerRegistrationService.update(cookerId, cooker);
        Cooker cookerCurrent = this.cookerRegistrationService.update(cookerId, cooker);
        return ResponseEntity.ok(assembler.toRepresentation(cookerCurrent));
    }

    @ApiOperation("Delete cooker")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Cooker deleted"),
            @ApiResponse(code = 404, message = "Cooker not found", response = Problem.class)
    })
    @DeleteMapping("/{Id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> remover(@ApiParam(value = "Cooker ID", example = "1") @PathVariable("Id") Long cookerId) {
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
