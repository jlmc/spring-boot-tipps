package io.github.jlmc.reactiveitemapiclient.controller;

import io.github.jlmc.reactiveitemapiclient.domain.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping(path = "/cli")
public class ItemClientController {

    WebClient webClient = WebClient.create("http://localhost:8080");

    @GetMapping("/retrieve")
    public Flux<Item> getAllItemsRetrive() {
        return webClient.get().uri("/v1/items")
                        .retrieve()
                        .bodyToFlux(Item.class)
                        .log("Items in Client Project");
    }

    @GetMapping("/exchange")
    public Flux<Item> getAllItemsExchange() {
        return webClient.get().uri("/v1/items")
                        //.exchange()
                        //.flatMapMany(clientResponse -> clientResponse.bodyToFlux(Item.class))
                        .exchangeToFlux(e -> e.bodyToFlux(Item.class))
                        .log("Items in Client Project");
    }

    @GetMapping("/retrieve/{id}")
    public Mono<Item> getOneItemsRetrieve(@PathVariable("id") String id) {
        return webClient.get().uri("/v1/items/{id}", id)
                        .retrieve()
                        .bodyToMono(Item.class)
                        .log("Items in Client Project");
    }

    @PostMapping("/create-retrieve")
    public Mono<Item> createItemWithRetrieve(@RequestBody Item item) {
        final Mono<Item> mono = Mono.just(item);
        return webClient.post().uri("/v1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mono, Item.class)
                        .retrieve()
                        .bodyToMono(Item.class)
                        .log("Items in Client Project: ");
    }

    @PostMapping("/create-exchange")
    public Mono<Item> createItemWithExchange(@RequestBody Item item) {
        final Mono<Item> mono = Mono.just(item);
        return webClient.post().uri("/v1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mono, Item.class)
                        .exchangeToMono(e -> e.bodyToMono(Item.class))
                        .log("Items in Client Project: ");
    }

    @PutMapping("update-retrieve/{id}")
    public Mono<Item> putItemWithRetrieve(@PathVariable("id") String id,
                                          @RequestBody Item item) {

        return webClient.put().uri("/v1/items/{id}", id)
                        .body(Mono.just(item), Item.class)
                        .retrieve()
                        .bodyToMono(Item.class)
                        .log();
    }

    @PutMapping("update-exchange/{id}")
    public Mono<Item> putItemWithExchange(@PathVariable("id") String id,
                                          @RequestBody Item item) {

        return webClient.put().uri("/v1/items/{id}", id)
                        .body(Mono.just(item), Item.class)
                        .exchangeToMono(e -> e.bodyToMono(Item.class))
                        .log();
    }

    @DeleteMapping("delete-retrieve/{id}")
    public Mono<Void> deleteWithRetrieve(@PathVariable("id") String id) {
        return webClient.delete().uri("/v1/items/{id}", id)
                        .retrieve()
                        .bodyToMono(Void.class)
                        .log();
    }

    @DeleteMapping("delete-exchange/{id}")
    public Mono<Void> deleteWithExchange(@PathVariable("id") String id) {
        return webClient.delete().uri("/v1/items/{id}", id)
                        .exchangeToMono(e -> e.bodyToMono(Void.class))
                        .log();
    }

    @GetMapping("/runtime-exception")
    public Flux<Item> getWithErrorHandler() {
        return webClient.get().uri("/v1/items/runtime-exception")
                        .retrieve()
                        .onStatus(
                                httpStatus -> HttpStatus.SERVICE_UNAVAILABLE == httpStatus,
                                clientResponse -> {
                                    Mono<String> errorMono = clientResponse.bodyToMono(String.class);
                                    return errorMono.flatMap(errorMessage -> {
                                        log.error("The error message is: {}", errorMessage);
                                        //throw new RuntimeException(errorMessage);
                                        return Mono.error(new RuntimeException(errorMessage));
                                    });
                                })
                        .bodyToFlux(Item.class)
                        .log("Items in Client Project");
    }

    @GetMapping("/runtime-exception2")
    public Flux<Object> getWithErrorHandler2() {
        return
        webClient.get().uri("/v1/items/runtime-exception")
                 .exchangeToFlux(clientResponse -> {
                    if (clientResponse.statusCode() == HttpStatus.SERVICE_UNAVAILABLE) {

                        return clientResponse.bodyToFlux(String.class)
                                             .flatMap(errorMessage -> {
                            log.error("The error message is: {}", errorMessage);
                            //throw new RuntimeException(errorMessage);
                            return Flux.error(new RuntimeException(errorMessage));
                        });

                    }

                     return clientResponse.bodyToFlux(Item.class);
                 });


        /*
        return
                webClient.get().uri("/v1/items/runtime-exception")
                         .exchange()
                         .flatMapMany(clientResponse -> {

                             if (clientResponse.statusCode().is5xxServerError()) {
                                 return clientResponse.bodyToMono(String.class)
                                                      .flatMap(errorMessage -> {
                                                          log.error("The error message is: {}", errorMessage);
                                                          throw new RuntimeException(errorMessage);
                                                      });
                             }

                             return clientResponse.bodyToFlux(Item.class);
                         });
         */
    }
}
