package com.example.demos.plugins.epic;

import com.example.demos.providers.Book;
import com.example.demos.providers.BookProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.Set;

@Component
@Qualifier("epic")
public class EpicBookProvider implements BookProvider {

    private final Set<Book> booksRegistry = Set.of(
        new Book("1-e", "Real work of java ee patterns"),
        new Book("2-e", "Clean Code")
    );

    @Override
    public Flux<Book> search(String title) {
        return Flux.fromIterable(booksRegistry)
                   .filter(it -> {

                       if (title == null || title.isBlank()) return true;

                       return it.title().toLowerCase().contains(title);
                   })
                   .sort(Comparator.comparing(Book::title));
    }

    @Override
    public Mono<Book> getByIsbn(String isbn) {
        return Flux.fromIterable(booksRegistry)
                   .filter(it -> it.isbn().equalsIgnoreCase(isbn))
                   .next();
    }

    @Override
    public String providerIdentifier() {
        return "epic";
    }
}
