package io.costax.food4u.api;

import io.costax.food4u.api.model.CookersXmlWrapper;
import io.costax.food4u.domain.model.Cooker;
import io.costax.food4u.domain.repository.CookerRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

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
    public ResponseEntity add(@RequestBody Cooker cooker, UriComponentsBuilder b) {
        final Cooker saved = repository.save(cooker);

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
        Cooker cookerCurrent = repository.findById(cookerId).orElse(null);

        if (cookerCurrent != null) {
            //cozinhaAtual.setNome(cozinha.getNome());
            BeanUtils.copyProperties(cooker, cookerCurrent, "id");

            cookerCurrent = repository.save(cookerCurrent);
            return ResponseEntity.ok(cookerCurrent);
        }

        return ResponseEntity.notFound().build();
    }
}
