package io.github.jlmc.reactive.api;

import io.github.jlmc.reactive.domain.model.Item;
import io.github.jlmc.reactive.domain.repository.ItemReactiveRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@Slf4j
@RequestMapping(path = "/v1/items")

public class ItemController {

    private final ItemReactiveRepository repository;

    public ItemController(ItemReactiveRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public Flux<Item> getAllItems() {

        log.info("In controller: ");
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Item>> getOneItem(@PathVariable("id") String id) {
        return repository.findById(id)
                  .map(ResponseEntity::ok)
                  .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Item>> create(@RequestBody @Valid Item item, ServerHttpRequest request) {
        return repository.save(item)
                .map(savedItem -> {
                    URI location = URI.create("/v1/items/" + savedItem.getId());
                    URI xLocation = URI.create(request.getURI() + "/" + savedItem.getId());
                    return ResponseEntity
                            .created(location)
                            .header("X-Location", xLocation.toString())
                            .body(savedItem);
                });
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable("id") String id) {
        return repository.deleteById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<Item>> update(@PathVariable("id") String id, @RequestBody Item item) {
        return repository.findById(id)
                         .flatMap(currentItem -> {

                             currentItem.setPrice(item.getPrice());
                             currentItem.setDescription(item.getDescription());
                             return repository.save(currentItem);

                         })
                         .map(ResponseEntity::ok)
                         .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/runtime-exception")
    public Flux<Item> runtimeException() {
        throw new RuntimeException("example");
    }
}
