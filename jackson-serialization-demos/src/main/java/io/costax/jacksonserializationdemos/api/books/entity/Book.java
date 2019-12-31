package io.costax.jacksonserializationdemos.api.books.entity;

import com.fasterxml.jackson.annotation.JsonView;
import io.costax.jacksonserializationdemos.api.books.jackson.Views;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Book {

    @JsonView({Views.BookNames.class, Views.BookSummary.class})
    @EqualsAndHashCode.Include
    private Integer id;
    @JsonView({Views.BookNames.class, Views.BookSummary.class})
    private String title;
    @JsonView({Views.BookSummary.class})
    private String isbn;
    private String description;
    private Author author;

}
