package io.costax.howtousemultipledatasources.domain.entities.books;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;

    public Book() {
    }

    public Book(final String name) {
        this.name = name;
    }

    public static Book of(final String name) {
        return new Book(name);
    }
}
