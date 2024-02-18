package io.github.jlmc.uof.domain.fruits.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Comparator;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Fruit(String id, String name, String description, String origin) {


    private static final Predicate<String> IS_NUMBER = Pattern.compile("\\d+").asPredicate();
    public static final Comparator<Fruit> BY_ID = (fruit0, fruit1) -> {
        if (IS_NUMBER.test(fruit0.id()) && IS_NUMBER.test(fruit1.id())) {
            return Long.valueOf(fruit0.id()).compareTo(Long.valueOf(fruit1.id()));
        } else {
            return fruit0.id().compareTo(fruit1.id());
        }
    };
}
