package io.github.jlmc.aop.api;

import io.github.jlmc.aop.core.audit.Audited;
import io.github.jlmc.aop.domain.model.Todo;
import io.github.jlmc.aop.domain.model.TodoSearchFilter;
import io.github.jlmc.aop.domain.service.TodoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;


//@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Validated
@RestController
@RequestMapping(
        path = "/todos",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class TodoController {

    static int count = 0;

    private final TodoService service;

    public TodoController(TodoService service) {
        count++;
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Todo>> getAll(
            WebRequest request,
            @RequestParam(value = "per_page", required = false) String perPage,
            TodoSearchFilter filter) {

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


    @Audited(operation = "Delete-Todo-Item", idParameterName = "id", severity = Audited.Severity.CRITICAL)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void remove(@PathVariable("id") Integer id) {
        service.remove(id);
    }
}
