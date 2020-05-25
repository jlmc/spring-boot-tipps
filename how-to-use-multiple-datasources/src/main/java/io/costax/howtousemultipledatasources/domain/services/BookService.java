package io.costax.howtousemultipledatasources.domain.services;

import io.costax.howtousemultipledatasources.domain.entities.books.Book;
import io.costax.howtousemultipledatasources.domain.repositories.books.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BookService {

    @Autowired
    BookRepository bookRepository;

    public Book add(final Book book) {
        return bookRepository.saveAndFlush(book);
    }
}
