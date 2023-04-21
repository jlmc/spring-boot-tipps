package io.costax.idoit.tasks.boundary;

import io.costax.idoit.tasks.control.TaskRepository;
import io.costax.idoit.tasks.entity.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/tasks")
public class TasksResource {

    private final TaskRepository repository;

    private final Tasks taskWitter;

    @Autowired
    public TasksResource(final TaskRepository repository, final Tasks taskWitter) {
        this.repository = repository;
        this.taskWitter = taskWitter;
    }

    @GetMapping("/echo")
    public ResponseEntity<?> echo() {
        return ResponseEntity.ok("tasks - " + System.currentTimeMillis());
    }

    @GetMapping
    public Page<Task> search(@PageableDefault(size = 5) Pageable pageable) {
        return repository.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> findByCode(@PathVariable Long id) {
        return repository.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Task> create(@Valid @RequestBody Task body, HttpServletResponse response) {

        final Task task = taskWitter.create(body);

        final URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(task.getId())
                .toUri();

        response.setHeader(HttpHeaders.LOCATION, uri.toASCIIString());

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove (@PathVariable  Long id) {
        taskWitter.remove(id);
    }

    /*
    @ExceptionHandler({NonExistentOrInactivePersonException.class})
    public ResponseEntity<Object> handlerNonExistentOrInactivePersonException(NonExistentOrInactivePersonException e) {
        String userMessage = messageSource.getMessage(e.getMessageKey(), new Object[0], LocaleContextHolder.getLocale());
        String devMessage = e.getCause() != null ? e.getCause().toString() : e.toString();

        return ResponseEntity.badRequest().body(Collections.singletonList(XMoneyExceptionHandler.Error.of(userMessage, devMessage)));
    }
    */

}
