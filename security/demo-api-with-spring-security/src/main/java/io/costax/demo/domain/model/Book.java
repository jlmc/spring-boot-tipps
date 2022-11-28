package io.costax.demo.domain.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

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

    public Book() {
    }

    public Integer getId() {
        return this.id;
    }

    public @NotBlank String getIsbn() {
        return this.isbn;
    }

    public String getTitle() {
        return this.title;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setIsbn(@NotBlank String isbn) {
        this.isbn = isbn;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Book)) return false;
        final Book other = (Book) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$isbn = this.getIsbn();
        final Object other$isbn = other.getIsbn();
        if (this$isbn == null ? other$isbn != null : !this$isbn.equals(other$isbn)) return false;
        final Object this$title = this.getTitle();
        final Object other$title = other.getTitle();
        if (this$title == null ? other$title != null : !this$title.equals(other$title)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Book;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $isbn = this.getIsbn();
        result = result * PRIME + ($isbn == null ? 43 : $isbn.hashCode());
        final Object $title = this.getTitle();
        result = result * PRIME + ($title == null ? 43 : $title.hashCode());
        return result;
    }

    public String toString() {
        return "Book(id=" + this.getId() + ", isbn=" + this.getIsbn() + ", title=" + this.getTitle() + ")";
    }
}
