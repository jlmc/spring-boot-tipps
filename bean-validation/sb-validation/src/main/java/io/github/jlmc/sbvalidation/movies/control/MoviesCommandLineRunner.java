package io.github.jlmc.sbvalidation.movies.control;

import io.github.jlmc.sbvalidation.movies.entity.Details;
import io.github.jlmc.sbvalidation.movies.entity.Movie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Stream;

@Component
public class MoviesCommandLineRunner implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(MoviesCommandLineRunner.class);
    @Autowired
    MoviesRepository moviesRepository;

    @Override
    public void run(String... args) {

        LOGGER.info("Populating the dev movies");

        Flux.fromStream(Stream.of(
                    Movie.createMovie(
                                 "Gladiator",
                                 "Ridley Scott",
                                 2000)
                         .setDetails(new Details("""
                                 Gladiator is a 2000 epic historical drama film directed by Ridley Scott and written by David Franzoni, 
                                 John Logan, and William Nicholson. 
                                 The film was co-produced and released by DreamWorks Pictures and Universal Pictures.
                                 """,
                                 "Russell Crowe",
                                 List.of("Russell Crowe",
                                         "Joaquin Phoenix",
                                         "Connie Nielsen",
                                         "Ralf Möller",
                                         "Oliver Reed",
                                         "Djimon Hounsou",
                                         "Derek Jacobi",
                                         "John Shrapnel",
                                         "Richard Harris",
                                         "Tommy Flanagan")
                         )),
                    Movie.createMovie("Joker", "Duke", 2019),
                    Movie.createMovie(
                            "Rambo - last blood", "Duke", 2020)
            ))
              .subscribe(moviesRepository::add);
    }
}
