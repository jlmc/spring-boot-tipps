package io.github.jlmc.sb.app.webflux.rest;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.stream.IntStream;

@Validated
@RestController
@RequestMapping("/todos")
public class TodosController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TodosController.class);

    private static TodoRepresentation todoOf(int it) {
        return TodoRepresentation.of(it, "todo-%d".formatted(it), "todo description %d".formatted(it));
    }

    @PostMapping
    public ResponseEntity<Mono<TodoRepresentation>> add(
            @RequestBody @Validated TodoRepresentation todoRepresentation,
            UriComponentsBuilder componentsBuilder,
            ServerHttpRequest serverHttpRequest) {
        LOGGER.info("add a new todo: {}", todoRepresentation);
        todoRepresentation.setId(12345);

        URI uri = componentsBuilder.uri(serverHttpRequest.getURI()).path("/{id}").build(todoRepresentation.getId());

        return ResponseEntity.created(uri).body(Mono.just(todoRepresentation));
    }

    @GetMapping
    public Mono<List<TodoRepresentation>> getPage(
            @RequestParam(value = "page", required = false, defaultValue = "1") @Positive Integer page,
            @RequestParam(value = "page_size", required = false, defaultValue = "10") @Positive @Max(50) Integer pageSize
    ) {
        LOGGER.info("get page [{}] with [{}] elements", page, pageSize);

        int skipCount = (page - 1) * pageSize;
        List<TodoRepresentation> todoRepresentations =
                IntStream.range(1, 105)
                         .mapToObj(TodosController::todoOf)
                         .skip(skipCount)
                         .limit(pageSize)
                         .toList();

        return Mono.just(todoRepresentations);
    }

    @GetMapping(path = "/{id}")
    public Mono<TodoRepresentation> getById(@PathVariable("id") @Pattern(regexp = "^\\d{1,5}$") String id) {
        return Mono.just(todoOf(Integer.parseInt(id)));
    }
}
