package io.costax.demo.domain.services;

import io.costax.demo.domain.exceptions.ResourceAlreadyResistedException;
import io.costax.demo.domain.exceptions.ResourceNotFoundException;
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

    @Transactional
    public Book update(Integer bookId, final Book book) {
        final Book book1 = bookRepository.findById(bookId).orElseThrow(() -> new ResourceNotFoundException(Book.class, bookId));

        book1.setTitle(book.getTitle());

        // just to ensure that all sql statements are executed before the return operator
        bookRepository.saveAndFlush(book1);

        return book1;
    }
}
