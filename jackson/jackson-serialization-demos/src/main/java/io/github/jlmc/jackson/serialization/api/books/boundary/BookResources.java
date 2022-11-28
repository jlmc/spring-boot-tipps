package io.github.jlmc.jackson.serialization.api.books.boundary;

import com.fasterxml.jackson.annotation.JsonView;
import io.github.jlmc.jackson.serialization.api.books.control.Books;
import io.github.jlmc.jackson.serialization.api.books.entity.Book;
import io.github.jlmc.jackson.serialization.api.books.jackson.Views;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookResources {

    @Autowired
    Books books;

    /**
     * http://localhost:8080/books
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Book> getAll() {
        return books.list();
    }

    @JsonView(Views.BookNames.class)
    @GetMapping(params = "projection=only-name")
    public List<Book> listOnlyNames() {
        return getAll();
    }

    @JsonView(Views.BookSummary.class)
    @GetMapping(params = "projection=summary")
    public List<Book> listSummaries() {
        return getAll();
    }

    /**
     * This can produce 3 types of output
     *
     * - http://localhost:8080/books/projections
     *
     * - http://localhost:8080/books/projections?type=name
     *
     * - http://localhost:8080/books/projections?type=summary
     */
    @GetMapping(value = "/projections")
    @ResponseStatus(HttpStatus.OK)
    public MappingJacksonValue getProjection(@RequestParam(required = false) String type) {
        final List<Book> all = getAll();

        MappingJacksonValue restaurantsWrapper = new MappingJacksonValue(all);

        if ("name".equals(type)) {
            restaurantsWrapper.setSerializationView(Views.BookNames.class);
        } else if ("summary".equals(type)) {
            restaurantsWrapper.setSerializationView(Views.BookSummary.class);
        }

        return restaurantsWrapper;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getById(@PathVariable("id") Integer id) {
        return books.book(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

}
