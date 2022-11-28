package io.github.jlmc.domain.entities.persons;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private int year;

    public Person() {
    }

    private Person(final String name, final int year) {
        this.name = name;
        this.year = year;
    }

    public static Person of(String name, int year) {
        return new Person(name, year);
    }
}
