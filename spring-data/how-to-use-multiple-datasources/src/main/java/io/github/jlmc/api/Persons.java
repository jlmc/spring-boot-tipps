package io.github.jlmc.api;


import io.github.jlmc.domain.entities.persons.Person;
import io.github.jlmc.domain.repositories.persons.PersonRepository;
import io.github.jlmc.domain.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        value = "/persons",
        produces = {
                MediaType.APPLICATION_JSON_VALUE
        })
public class Persons {

    @Autowired
    PersonRepository repository;

    @Autowired
    PersonService personService;

    @GetMapping
    public List<Person> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Person person) {
        Person saved = personService.add(person);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(saved);
    }
}
