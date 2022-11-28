package io.costax.versioningapis.uris.api.v1;

import io.costax.versioningapis.uris.api.ResourceUriHelper;
import io.costax.versioningapis.uris.api.v1.model.BookDtoV1;
import io.costax.versioningapis.uris.core.web.ApiExampleMediaTypes;
import io.costax.versioningapis.uris.domain.model.Book;
import io.costax.versioningapis.uris.domain.repository.Books;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(
        value = "/books",
        produces = {
                ApiExampleMediaTypes.V1_APPLICATION_JSON_VALUE
        })
public class BookControllerV1 {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookControllerV1.class);

    private final Books books;

    public BookControllerV1(final Books books) {
        this.books = books;
    }

    @GetMapping
    private List<BookDtoV1> all() {
        LOGGER.info("-- GET all the Books V1");

        return books
                .list()
                .stream()
                .map(BookDtoV1::from)
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getBook(@PathVariable("id") Integer id) {
        return books.findById(id)
                .map(BookDtoV1::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BookDtoV1> add(BookDtoV1 payload) {
        Book book = assemblerToEntity(payload);

        Book saved = books.add(book);

        ResourceUriHelper.addUriInResponseHeader(saved.getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(BookDtoV1.from(saved));
    }

    private Book assemblerToEntity(final BookDtoV1 payload) {
        return Book.of(payload.getId(), payload.getIsbn(), payload.getTitle());
    }

}
