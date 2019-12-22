package io.costax.food4u.api;

import io.costax.food4u.api.model.CookersXmlWrapper;
import io.costax.food4u.domain.exceptions.CookerNotFoundException;
import io.costax.food4u.domain.exceptions.ResourceInUseException;
import io.costax.food4u.domain.exceptions.ResourceNotFoundException;
import io.costax.food4u.domain.model.Cooker;
import io.costax.food4u.domain.repository.CookerRepository;
import io.costax.food4u.domain.services.CookerRegistrationService;
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
public class CookerResources {

    private final CookerRepository repository;
    private final CookerRegistrationService cookerRegistrationService;

    public CookerResources(final CookerRepository repository,
                           final CookerRegistrationService cookerRegistrationService) {
        this.repository = repository;
        this.cookerRegistrationService = cookerRegistrationService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Cooker> list() {
        return repository.findAll();
    }

    @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
    public CookersXmlWrapper listXml() {
        return new CookersXmlWrapper(repository.findAll());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{Id}")
    public Cooker getById(@PathVariable("Id") Long cookerId) {
        return repository.findById(cookerId)
                //.map(ResponseEntity::ok)
                .orElseThrow(() -> CookerNotFoundException.of(cookerId));
    }

    @PostMapping
    //@ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Cooker> add(@RequestBody @Valid Cooker cooker, UriComponentsBuilder b) {
        Cooker saved = cookerRegistrationService.add(cooker);

        //UriComponents uriComponents = b.path("/cookers/{id}").buildAndExpand(saved.getId());
        //final URI uri = uriComponents.toUri();

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();

        return ResponseEntity.created(location).body(saved);
    }

    @PutMapping("/{Id}")
    public ResponseEntity<Cooker> atualizar(@PathVariable("Id") Long cookerId,
                                            @RequestBody Cooker cooker) {
        //Cooker cookerCurrent = cookerRegistrationService.update(cookerId, cooker);
        Cooker cookerCurrent = this.cookerRegistrationService.update(cookerId, cooker);
        return ResponseEntity.ok(cookerCurrent);
    }


    @DeleteMapping("/{Id}")
    public ResponseEntity<Cooker> remover(@PathVariable("Id") Long cookerId) {
        this.cookerRegistrationService.remove(cookerId);
        return ResponseEntity.noContent().build();
    }

    @ResponseBody
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
    }

}
