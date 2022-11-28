package io.github.jlmc.jackson.serialization.api.movies.boundary;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import io.github.jlmc.jackson.serialization.api.movies.control.Movies;
import io.github.jlmc.jackson.serialization.api.movies.entity.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieResources {

    @Autowired
    Movies movies;

//    @GetMapping
//    @ResponseStatus(HttpStatus.OK)
//    public List<Movie> getAll() {
//        return movies.getMovies();
//    }

    /**
     * localhost:8080/movies/dynamic-filter?movie-fields=
     * [ id, title, rating, price, director ]
     * &director-fields=
     * [ nick, name ]
     */
    @GetMapping(value = "/dynamic-filter")
    @ResponseStatus(HttpStatus.OK)
    public MappingJacksonValue getAllDynamicFilter(
            @RequestParam(required = false, name = "movie-fields") String movieFields,
            @RequestParam(required = false, name = "director-fields") String directorFields
    ) {
        final List<Movie> movies = this.movies.getMovies();

        MappingJacksonValue restaurantsWrapper = new MappingJacksonValue(movies);

        SimpleFilterProvider filterProvider = new SimpleFilterProvider();
        filterProvider.addFilter("movie-filter", SimpleBeanPropertyFilter.serializeAll());
        filterProvider.addFilter("director-filter", SimpleBeanPropertyFilter.serializeAll());


        boolean includeTheDirector = true;
        if (movieFields != null && !movieFields.isBlank()) {
            final String[] split = Arrays.stream(movieFields.split(","))
                    .map(String::trim)
                    .toArray(String[]::new);

            includeTheDirector = Arrays.asList(split).contains("director");

            filterProvider.addFilter("movie-filter", SimpleBeanPropertyFilter.filterOutAllExcept(split));
        }

        if (includeTheDirector) {
            if (directorFields != null && !directorFields.isBlank()) {
                final String[] split = Arrays.stream(directorFields.split(","))
                        .map(String::trim)
                        .toArray(String[]::new);
                filterProvider.addFilter("director-filter", SimpleBeanPropertyFilter.filterOutAllExcept(split));
            }
        }

        restaurantsWrapper.setFilters(filterProvider);

        return restaurantsWrapper;
    }
}
