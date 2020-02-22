package io.costax.demo.api;


import io.costax.demo.domain.model.Book;
import io.costax.demo.domain.repositories.BookRepository;
import io.costax.demo.domain.repositories.BookSpecifications;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        value = "/books",
        produces = {
                MediaType.APPLICATION_JSON_VALUE
        })
public class BooksController {

    private final BookRepository bookRepository;

    public BooksController(final BookRepository bookRepository) {
        this.bookRepository = bookRepository;
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

}
