package io.github.jlmc.jackson.serialization.api.movies.entity;

import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.Data;

import java.math.BigDecimal;

@JsonFilter("movie-filter")
@Data
public class Movie {

    private Integer id;
    private String title;
    private int rating;
    private BigDecimal price;

    private Director director;
}
