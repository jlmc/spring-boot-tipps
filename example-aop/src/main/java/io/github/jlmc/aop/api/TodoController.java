package io.github.jlmc.aop.api;

import io.github.jlmc.aop.domain.model.Todo;
import io.github.jlmc.aop.domain.service.TodoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
        path = "/todos",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class TodoController {

    private final TodoService service;

    public TodoController(TodoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Todo>> getAll() {
        return ResponseEntity.ok(service.all());
    }

    @PostMapping
    public ResponseEntity<Todo> add(@Validated @RequestBody Todo input) {
        Todo added = service.add(input);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(added.getId())
                .toUri();

        return ResponseEntity.created(location).body(added);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void remove(@PathVariable("id") Integer id) {
        service.remove(id);
    }
}
