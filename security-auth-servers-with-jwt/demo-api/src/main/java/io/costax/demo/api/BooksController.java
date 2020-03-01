package io.costax.demo.api;


import io.costax.demo.core.security.authorities.CheckSecurity;
import io.costax.demo.domain.model.Book;
import io.costax.demo.domain.repositories.BookRepository;
import io.costax.demo.domain.repositories.BookSpecifications;
import io.costax.demo.domain.services.AddBookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(
        value = "/books",
        produces = {
                MediaType.APPLICATION_JSON_VALUE
        })
public class BooksController {

    private final BookRepository bookRepository;
    private final AddBookService addBookService;

    public BooksController(final BookRepository bookRepository, final AddBookService addBookService) {
        this.bookRepository = bookRepository;
        this.addBookService = addBookService;
    }

    @GetMapping
    public List<Book> list() {
        return bookRepository.findAll();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Integer id) {
        return bookRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/i/{isbn}")
    public ResponseEntity<?> findByIsbn(@PathVariable("isbn") String isbn) {
        return bookRepository.findOne(BookSpecifications.withISBN(isbn))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //@PreAuthorize("hasAnyAuthority('EDIT_BOOKS')")
    @CheckSecurity.Books.CanCreateOrEdit
    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody @Valid Book book) {
        final Book added = addBookService.add(book);

        return ResponseEntity.created(ResourceUriHelper.getUri(added.getId())).body(added);
    }

}
