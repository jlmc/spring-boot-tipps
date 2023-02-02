package io.github.jlmc.sb.app.mvc.interfaces.rest;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Stream;

@Validated
@RestController
@RequestMapping("/todos")
public class TodosController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TodosController.class);

    @PostMapping
    public ResponseEntity<TodoRepresentation> add(@RequestBody @Validated TodoRepresentation todoRepresentation) {
        LOGGER.info("add a new todo: {}", todoRepresentation);
        return ResponseEntity.ok(todoRepresentation);
    }

    @GetMapping
    public List<TodoRepresentation> getPage(
            @RequestParam(value = "page", required = false, defaultValue = "1") @Positive Integer page,
            @RequestParam(value = "page_size", required = false, defaultValue = "10") @Positive @Max(50) Integer size
    ) {
        return Stream.of(
                TodoRepresentation.of("todo-1", "description-1"),
                TodoRepresentation.of("todo-2", "description-2")
        ).toList();
    }

    @GetMapping("/{todo_id}")
    public TodoRepresentation getById(@PathVariable(value = "todo_id") @Pattern(regexp = "^\\d{1,5}$")  String id) {
        TodoRepresentation todo = TodoRepresentation.of("todo-1", "description-1");
        todo.setId(Integer.parseInt(id));
        return todo;
    }
}
