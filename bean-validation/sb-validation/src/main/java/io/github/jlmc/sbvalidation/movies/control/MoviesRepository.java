package io.github.jlmc.sbvalidation.movies.control;

import io.github.jlmc.sbvalidation.movies.entity.Movie;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;


@Repository
public class MoviesRepository {

    AtomicLong sequence = new AtomicLong(0);

    Map<String, Movie> movies = new HashMap<>();

    public Mono<Movie> add(Movie movie) {
        if (movie.getId() != null) {
            throw new IllegalArgumentException();
        }

        String id = "" + sequence.incrementAndGet();
        movie.setId(id);
        movies.put(id, movie);
        return Mono.just(movie);
    }

    public Flux<Movie> all() {
        return Flux.fromIterable(movies.values())
                   .map(f -> f)
                   .sort(Comparator.comparing(Movie::getId))
                   .log()
                   .delayElements(Duration.ofMillis(50));
    }

    public Mono<Movie> findById(String id) {
        return Optional.ofNullable(movies.get(id))
                       .map(Mono::just)
                       .orElse(Mono.empty());
    }
}
