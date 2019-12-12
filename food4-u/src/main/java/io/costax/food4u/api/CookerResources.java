package io.costax.food4u.api;

import io.costax.food4u.domain.model.Cooker;
import io.costax.food4u.domain.repository.CookerRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public List<Cooker> list() {
        return repository.findAll();
    }

    //@ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") Long cookerId) {
        return repository.findById(cookerId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
