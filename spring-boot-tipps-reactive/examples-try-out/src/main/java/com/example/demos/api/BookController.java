package com.example.demos.api;


import com.example.demos.providers.Book;
import com.example.demos.providers.BookProvider;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@RestController
@Validated
@Slf4j
@RequestMapping(path = "/books", produces = MediaType.APPLICATION_JSON_VALUE)
public class BookController {

    private final Map<String, BookProvider> providers;

    public BookController(Set<BookProvider> providers) {
        this.providers =
                providers.stream()
                         .collect(toMap(
                                 BookProvider::providerIdentifier,
                                 Function.identity()));
    }

    @GetMapping
    public Mono<QueryParameterFilters> getAllItems(QueryParameterFilters filters) {
        log.info("The received parameter are: " + filters);

        return Mono.just(filters);
    }

    @GetMapping("/search")
    public Flux<Book> search(
            @Validated QueryParameterFilters filters) {

        return Flux.fromIterable(providers.values())
                   .flatMap(it -> it.search(filters.getTitle()))
                   .sort(Comparator.comparing(Book::title));
    }

    @GetMapping("/{provider}/search")
    public Flux<Book> searchInProvider(
            @PathVariable String provider,
            @Validated QueryParameterFilters filters) {
        return getProvider(provider)
                .log()
                .flux()
                .flatMap(it -> it.search(filters.getTitle()))
                .sort(Comparator.comparing(Book::title));
    }

    @GetMapping("/{provider}/{isbn}")
    public Mono<ResponseEntity<Book>> getBookInProviderByIsbn(
            @NotBlank @PathVariable String provider,
            @NotBlank @PathVariable String isbn) {

        return getProvider(provider)
                .flatMap(it -> it.getByIsbn(isbn))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    private Mono<BookProvider> getProvider(String provider) {
        return Optional.ofNullable(providers.get(provider))
                       .map(Mono::just)
                       .orElseGet(() -> Mono.error(ProviderNotFoundException::new));
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "The given provider is not supported")
    static class ProviderNotFoundException extends RuntimeException {
    }

}
