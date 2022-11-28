package com.example.demos.providers;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BookProvider {
    Flux<Book> search(String title);

    Mono<Book> getByIsbn(String isbn);
    
    String providerIdentifier();
}
