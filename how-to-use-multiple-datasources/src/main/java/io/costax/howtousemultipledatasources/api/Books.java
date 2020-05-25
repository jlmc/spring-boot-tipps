package io.costax.howtousemultipledatasources.api;

import io.costax.howtousemultipledatasources.domain.entities.books.Book;
import io.costax.howtousemultipledatasources.domain.repositories.books.BookRepository;
import io.costax.howtousemultipledatasources.domain.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class Books {

    @Autowired
    BookRepository repository;

    @Autowired
    BookService bookService;

    @GetMapping
    public List<Book> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Book book) {
        Book saved = bookService.add(book);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(saved);
    }
}
