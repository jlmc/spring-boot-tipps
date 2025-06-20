package io.github.jlmc.jackson.serialization.api.books.control;

import io.github.jlmc.jackson.serialization.api.books.entity.Author;
import io.github.jlmc.jackson.serialization.api.books.entity.Book;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class Books {

    private Map<Integer, Book> books = new HashMap<>();

    @PostConstruct
    protected void init() {

        Author author1 = new Author();
        author1.setId(1);
        author1.setName("Saramago");

        Author author2 = new Author();
        author2.setId(2);
        author2.setName("Camoes");

        final Map<Integer, Book> collects = IntStream
                .rangeClosed(1, 5)
                .mapToObj(i -> {
                    Book b = new Book();
                    b.setId(i);
                    b.setIsbn("I123-" + i);
                    b.setDescription("Some thing just for testing " + i);
                    b.setTitle("Book " + i + " How to");

                    if (i % 2 == 0) {
                        b.setAuthor(author2);
                    } else {
                        b.setAuthor(author1);
                    }
                    return b;
                })
                .collect(Collectors.toMap(Book::getId, Function.identity()));

        books.putAll(collects);
    }

    public List<Book> list() {
        return books.values()
                .stream()
                .sorted(Comparator.comparingInt(Book::getId))
                .collect(Collectors.toUnmodifiableList());
    }

    public Optional<Book> book(Integer id) {
        return Optional.ofNullable(books.get(id));
    }
}
