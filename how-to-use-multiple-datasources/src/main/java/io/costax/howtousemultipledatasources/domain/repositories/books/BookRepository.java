package io.costax.howtousemultipledatasources.domain.repositories.books;

import io.costax.howtousemultipledatasources.domain.entities.books.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
}
