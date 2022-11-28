package io.github.jlmc.domain.repositories.persons;

import io.github.jlmc.domain.entities.persons.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {
}
