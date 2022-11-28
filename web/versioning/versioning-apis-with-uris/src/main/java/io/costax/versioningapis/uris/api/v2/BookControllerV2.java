package io.costax.versioningapis.uris.api.v2;

import io.costax.versioningapis.uris.api.ResourceUriHelper;
import io.costax.versioningapis.uris.api.v2.model.BookDtoV2;
import io.costax.versioningapis.uris.domain.model.Book;
import io.costax.versioningapis.uris.domain.repository.Books;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(
        value = "/v2/books",
        produces = {
                MediaType.APPLICATION_JSON_VALUE
        })
public class BookControllerV2 {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookControllerV2.class);

    private final Books books;

    public BookControllerV2(final Books books) {
        this.books = books;
    }

    @GetMapping
    private List<BookDtoV2> all() {
        LOGGER.info("-- GET all the Books V2");

        return books
                .list()
                .stream()
                .map(BookDtoV2::from)
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getBook(@PathVariable("id") Integer id) {
        return books.findById(id)
                .map(BookDtoV2::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BookDtoV2> add(BookDtoV2 payload) {
        Book book = assemblerToEntity(payload);

        Book saved = books.add(book);

        ResourceUriHelper.addUriInResponseHeader(saved.getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(BookDtoV2.from(saved));
    }

    private Book assemblerToEntity(final BookDtoV2 payload) {
        final Book book = Book.of(payload.getId(), payload.getIsbn(), payload.getTitle());
        book.setDescription(payload.getDescription());
        return book;
    }

}
