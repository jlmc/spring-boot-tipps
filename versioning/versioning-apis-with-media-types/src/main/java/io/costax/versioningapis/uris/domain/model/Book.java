package io.costax.versioningapis.uris.domain.model;

import lombok.Data;

@Data
public class Book {
    private Integer id;
    private String isbn;
    private String title;

    // property added in the version 2
    private String description;

    public Book() {
    }

    private Book(final Integer id, final String isbn, final String title) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
    }

    public static Book of(final Integer id, final String isbn, final String title) {
        return new Book(id, isbn, title);
    }
}
