package io.github.jlmc.sbvalidation.movies.entity;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.util.Assert;

import java.util.Objects;

public class Movie {

    private String id;
    @NotBlank
    private String title;
    private String director;

    @Min(1900)
    private int year;

    @Valid
    private Details details = null;

    public Movie() {
    }

    private Movie(String title, String director, int year) {
        this.title = title;
        this.director = director;
        this.year = year;
    }

    public static Movie createMovie(String title, String director, int year) {
        Assert.hasLength(title, "title argument is required; it must not be null");
        Assert.hasLength(director, "director argument is required; it must not be null");

        return new Movie(title, director, year);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Movie movie)) return false;

        return id != null && Objects.equals(id, movie.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 31;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                '}';
    }

    public Movie setDetails(Details details) {
        this.details = details;
        return this;
    }

    public Details getDetails() {
        return details;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
