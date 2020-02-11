package io.costax.versioningapis.uris.domain.repository;

import io.costax.versioningapis.uris.domain.model.Book;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class Books {

    private AtomicInteger sequence = new AtomicInteger(0);
    private List<Book> books = new ArrayList<>();

    @PostConstruct
    protected void init() {
        final List<Book> bs = IntStream
                .rangeClosed(1, 3)
                .mapToObj(i -> {
                    final Book of = Book.of(i, "isbn-" + i, "Book - " + i);
                    of.setDescription("Duke wooten " + i);
                    return of;
                })
                .collect(Collectors.toList());

        Integer max = bs.stream().map(Book::getId).max(Integer::compareTo).orElse(0);
        sequence.set(max);
        books.addAll(bs);
    }

    public List<Book> list() {
        return List.copyOf(books);
    }

    public Optional<Book> findById(final Integer id) {
        return books
                .stream()
                .filter(x -> Objects.equals(id, x.getId()))
                .findFirst();
    }

    public Book add(final Book book) {
        book.setId(sequence.incrementAndGet());
        books.add(book);
        return book;
    }
}
