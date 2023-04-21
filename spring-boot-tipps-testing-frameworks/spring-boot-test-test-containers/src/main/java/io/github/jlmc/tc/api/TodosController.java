package io.github.jlmc.tc.api;

import io.github.jlmc.tc.domain.todos.entities.Todo;
import io.github.jlmc.tc.domain.todos.repositories.TodoRepository;
import io.github.jlmc.tc.domain.todos.services.TodoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Validated
@RestController
@RequestMapping(
        value = "/todos",
        produces = MediaType.APPLICATION_JSON_VALUE)
public class TodosController {

    private final TodoService todoService;
    private final TodoRepository todoRepository;

    public TodosController(TodoService todoService, TodoRepository todoRepository) {
        this.todoService = todoService;
        this.todoRepository = todoRepository;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Todo> getById(@PathVariable("id") Long id) {
        return todoRepository.findById(id)
                             .map(ResponseEntity::ok)
                             .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Todo>> getAll() {
        return ResponseEntity.ok(todoRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Todo> create(@Validated @RequestBody Todo todo) {
        Todo added = todoService.add(todo);

        /*
         UriComponentsBuilder b) {
         UriComponents uriComponents = b.path("/cookers/{id}").buildAndExpand(saved.getId());
        final URI uri = uriComponents.toUri();
        */

        URI uri =
                ServletUriComponentsBuilder.fromCurrentRequest()
                                           .path("/{id}")
                                           .buildAndExpand(added.getId())
                                           .toUri();

        return ResponseEntity.created(uri)
                             .body(added);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(@PathVariable("id") Long id) {
        todoService.delete(id);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Todo> update(@PathVariable("id") Long id, @Validated @RequestBody Todo todo) {
        return todoService.update(id, todo)
                          .map(ResponseEntity::ok)
                          .orElseGet(() -> ResponseEntity.notFound().build());
    }
}


/*
                                                      UriComponentsBuilder b) {
        final User user = userAssembler.toDomainObject(payload);
        User saved = userRegistrationService.sign(user);

        //UriComponents uriComponents = b.path("/cookers/{id}").buildAndExpand(saved.getId());
        //final URI uri = uriComponents.toUri();

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
 */
