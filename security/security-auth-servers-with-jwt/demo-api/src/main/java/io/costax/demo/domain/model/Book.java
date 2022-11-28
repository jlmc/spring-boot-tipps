package io.costax.demo.domain.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Data
@Entity
@Table(name = "t_book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Column(name = "isbn", unique = true, nullable = false)
    private String isbn;

    private String title;

    private Integer author;

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Book book = (Book) o;
        return getId() != null && Objects.equals(id, book.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
