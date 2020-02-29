package io.costax.demo.domain.services;

import io.costax.demo.domain.exceptions.ResourceAlreadyResistedException;
import io.costax.demo.domain.model.Book;
import io.costax.demo.domain.repositories.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddBookService {

    private final BookRepository bookRepository;

    public AddBookService(final BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional
    public Book add(final Book book) {

        if (bookRepository.existsByIsbnIgnoreCase(book.getIsbn())) {
            throw new ResourceAlreadyResistedException("Book with isbn '%s' is already register", book.getIsbn());
        }

        return bookRepository.saveAndFlush(book);
    }
}
