package io.github.jlmc.sbvalidation.movies.boundary;

import io.github.jlmc.sbvalidation.movies.control.MoviesRepository;
import io.github.jlmc.sbvalidation.movies.entity.Movie;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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

@Validated
@RestController
@RequestMapping(path = MoviesConstants.MOVIES)
public class MoviesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MoviesController.class);

    @Autowired
    MoviesRepository moviesRepository;

    @GetMapping
    public Flux<Movie> getMovies(
            @Min(0)
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page ,
            @Min(1)
            @Max(50)
            @RequestParam(value = "page_size", required = false, defaultValue = "10") Integer limit,

            @Validated MovieFilter filter

    ) {
        LOGGER.info("PAGINATION: [page: {}, limit: {}, filter: {}]", page, limit, filter);

        return moviesRepository.all().doFirst(() -> LOGGER.info("GET movies"));
    }

    @GetMapping(path = "/{id}")
    public Mono<ResponseEntity<Movie>> getById(@NotBlank @PathVariable String id) {
        Mono<ResponseEntity<Movie>> alternative = Mono.just(ResponseEntity.<Movie>notFound().build());

        return moviesRepository.findById(id)
                               .doFirst(() -> LOGGER.info("GET movie [{}]", id))
                               .map(ResponseEntity::ok)
                               .switchIfEmpty(alternative)
                ;
    }

    @PostMapping
    public Mono<ResponseEntity<Movie>> addMovie(@RequestBody @Validated Movie payload, UriComponentsBuilder b) {

        Mono<ResponseEntity<Movie>> alternative = Mono.just(ResponseEntity.<Movie>unprocessableEntity().build());
        return moviesRepository.add(payload)
                               .doFirst(() -> LOGGER.info("POST new-movie"))
                               .map(it -> ResponseEntity.created(b.path("/movies/{id}").build(it.getId()))
                                         .body(it))
                               .switchIfEmpty(alternative);
    }
}
