package io.github.jlmc.domain.services;


import io.github.jlmc.domain.entities.books.Book;
import io.github.jlmc.domain.entities.persons.Person;
import io.github.jlmc.domain.repositories.books.BookRepository;
import io.github.jlmc.domain.repositories.persons.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Order(0)
@Service
public class AppListener {

    @Autowired
    @Lazy
    AppListener self;

    @Autowired
    BookRepository bookRepository;
    @Autowired
    PersonRepository personRepository;

    @EventListener
    public void onApplicationReady(ApplicationReadyEvent doesntMatter) {
        self.generate();
    }

    @Transactional
    public void generate() {

        personRepository.save(Person.of("A", 2020));
        bookRepository.save(Book.of("Example of book"));
       // personRepository.flush();
    }
}
