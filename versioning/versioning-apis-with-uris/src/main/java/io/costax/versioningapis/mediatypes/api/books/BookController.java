package io.costax.versioningapis.mediatypes.api.books;

import io.costax.versioningapis.mediatypes.api.ResourceUriHelper;
import io.costax.versioningapis.mediatypes.api.books.entity.Book;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping(
        value = "/books",
        produces = {
                MediaType.APPLICATION_JSON_VALUE,
        })
public class BookController {

    private AtomicInteger sequence = new AtomicInteger(0);
    private List<Book> books = new ArrayList<>();

    @PostConstruct
    protected void init() {
        final List<Book> bs = IntStream
                .rangeClosed(1, 3)
                .mapToObj(i -> Book.of(i, "isbn-" + i, "Book - " + i))
                .collect(Collectors.toList());

        Integer max = bs.stream().map(Book::getId).max(Integer::compareTo).orElse(0);
        sequence.set(max);
        books.addAll(bs);
    }

    @GetMapping
    private List<Book> all() {
        return books;
    }

    @GetMapping(path = "/{id}")
    public  ResponseEntity<?> getBook(@PathVariable("id") Integer id) {
        return books
                .stream()
                .filter(x -> Objects.equals(x.getId(), id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Book> add(Book book) {
        book.setId(sequence.incrementAndGet());
        books.add(book);

        ResourceUriHelper.addUriInResponseHeader(book.getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(book);
    }

}
