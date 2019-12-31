package io.costax.jacksonserializationdemos.api.movies.control;

import io.costax.jacksonserializationdemos.api.movies.entity.Director;
import io.costax.jacksonserializationdemos.api.movies.entity.Movie;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class Movies {

    private List<Movie> movies = List.of();

    @PostConstruct
    protected void init() {

        final Director director = new Director();
        director.setName("Tarantino");
        director.setNick("Tino");

        this.movies = IntStream
                .rangeClosed(1, 5)
                .mapToObj(id -> {
                    final Movie movie = new Movie();
                    movie.setId(id);
                    movie.setPrice(BigDecimal.TEN);
                    movie.setRating(5);
                    movie.setTitle("Movie xpt " + id);
                    movie.setDirector(director);
                    return movie;
                })
                .collect(Collectors.toUnmodifiableList());
    }

    public List<Movie> getMovies() {
        return movies;
    }
}
