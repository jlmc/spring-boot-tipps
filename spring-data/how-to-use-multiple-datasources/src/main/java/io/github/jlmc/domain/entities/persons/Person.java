package io.github.jlmc.domain.entities.persons;

import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

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
