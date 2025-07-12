package io.github.jlmc.domain.entities.books;

import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

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
