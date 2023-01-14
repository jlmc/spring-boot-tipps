package io.github.jlmc.sbvalidation.movies.boundary;

import io.github.jlmc.sbvalidation.movies.control.MoviesRepository;
import io.github.jlmc.sbvalidation.movies.entity.Movie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/movies")
public class MoviesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MoviesController.class);

    @Autowired
    MoviesRepository moviesRepository;

    @GetMapping
    public Flux<Movie> getMovies(
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page ,
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit
    ) {
        LOGGER.info("PAGINATION: [page: {}, limit: {}]", page, limit);

        return moviesRepository.all();
    }

    @GetMapping(path = "/{id}")
    public Mono<Movie> getById(@PathVariable String id) {
        Mono<Movie> result = moviesRepository.findById(id);

        return result;
    }

    @PostMapping
    public Mono<ResponseEntity<Movie>> addMovie(@RequestBody Movie payload, UriComponentsBuilder b) {

        Mono<ResponseEntity<Movie>> alternative = Mono.just(ResponseEntity.<Movie>notFound().build());
        return moviesRepository.add(payload)
                               .map(it -> ResponseEntity.created(b.path("/movies/{id}").build(it.getId()))
                                         .body(it))
                               .switchIfEmpty(alternative);
    }
}
