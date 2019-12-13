package io.costax.food4u.api;

import io.costax.food4u.api.model.CookersXmlWrapper;
import io.costax.food4u.domain.model.Cooker;
import io.costax.food4u.domain.repository.CookerRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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

    public CookerResources(final CookerRepository repository) {
        this.repository = repository;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Cooker> list() {
        return repository.findAll();
    }

    @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
    public CookersXmlWrapper listXml() {
        return new CookersXmlWrapper(repository.findAll());
    }

    //@ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") Long cookerId) {
        return repository.findById(cookerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    //@ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity add(@RequestBody Cooker cooker) {
        final Cooker saved = repository.save(cooker);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();

        return ResponseEntity.created(location).body(saved);
    }
}
