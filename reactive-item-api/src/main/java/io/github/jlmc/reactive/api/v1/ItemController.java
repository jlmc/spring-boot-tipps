package io.github.jlmc.reactive.api.v1;

import io.github.jlmc.reactive.ItemConstants;
import io.github.jlmc.reactive.domain.model.Item;
import io.github.jlmc.reactive.domain.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequestMapping(path = ItemConstants.ITEM_END_POINT_V1)

public class ItemController {

    private final ItemReactiveRepository repository;

    public ItemController(ItemReactiveRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public Flux<Item> getAllItems() {

        System.out.println("In controller: ");
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Item>> getOneItem(@PathVariable("id") String id) {
        Mono<ResponseEntity<Item>> mono =
                repository.findById(id)
                          .map(ResponseEntity::ok)
                          .defaultIfEmpty(ResponseEntity.notFound().build());
        return mono;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Item> create(@RequestBody Item item) {
        return repository.save(item);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> create(@PathVariable("id") String id) {
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
        return repository.findAll()
                         .concatWith(Mono.error(new RuntimeException("some error!")));
    }

    /*
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handlerRuntimeException(RuntimeException exception) {
        log.error("Exception caught in handlerRuntimeException : {}", exception.getMessage(), exception );
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(exception.getMessage());
    }

     */
}
