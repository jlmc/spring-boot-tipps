package io.costax.howtousemultipledatasources.domain.services;

import io.costax.howtousemultipledatasources.domain.entities.persons.Person;
import io.costax.howtousemultipledatasources.domain.repositories.persons.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PersonService {

    @Autowired
    PersonRepository personRepository;

    @Transactional(transactionManager = "personsTransactionManager")
    public Person add(final Person person) {
        return personRepository.saveAndFlush(person);
    }
}
