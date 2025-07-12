package io.github.jlmc.reactive.handler;

import io.github.jlmc.reactive.domain.model.Item;
import io.github.jlmc.reactive.domain.repository.ItemReactiveRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
public class ItemsHandler {

    private static final Mono<ServerResponse> NOT_FOUND = ServerResponse.notFound().build();
    private final ItemReactiveRepository repository;

    public ItemsHandler(ItemReactiveRepository repository) {
        this.repository = repository;
    }

    public Mono<ServerResponse> getAllItems(ServerRequest serverRequest) {
        return ServerResponse.ok()
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(repository.findAll(), Item.class);
    }

    public Mono<ServerResponse> getOneItem(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        Mono<Item> item = repository.findById(id);
        return
                item.flatMap((Item i) -> ServerResponse.ok()
                                                       .contentType(MediaType.APPLICATION_JSON)
                                                       .body(fromValue(i)))
                    .switchIfEmpty(NOT_FOUND);
    }

    public Mono<ServerResponse> deleteItem(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");

        return ServerResponse.noContent()
                             .build(repository.deleteById(id));
    }

    public Mono<ServerResponse> createItem(ServerRequest serverRequest) {
        Mono<Item> itemMono = serverRequest.bodyToMono(Item.class);
        return itemMono.flatMap(repository::save)
                       .flatMap(i -> ServerResponse.status(HttpStatus.CREATED)
                                                   .contentType(MediaType.APPLICATION_JSON)
                                                   //.body(BodyInserters.fromObject(item)))
                                                   .body(fromValue(i)));
    }

    public Mono<ServerResponse> updateItem(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        Mono<Item> itemMono = serverRequest.bodyToMono(Item.class);
        return itemMono.flatMap(item -> repository.findById(id)
                                                  .flatMap(currentItem -> {
                                                      currentItem.setPrice(item.getPrice());
                                                      currentItem.setDescription(item.getDescription());
                                                      return repository.save(currentItem);
                                                  }))
                       .flatMap(i -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(fromValue(i)))
                       .switchIfEmpty(NOT_FOUND);

    }

    public Mono<ServerResponse> itemRuntimeException(ServerRequest serverRequest) {
         throw new RuntimeException("My Fun Runtime exception");
    }
}
