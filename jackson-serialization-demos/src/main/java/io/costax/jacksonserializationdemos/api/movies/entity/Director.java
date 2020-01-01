package io.costax.jacksonserializationdemos.api.movies.entity;

import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.Data;

@JsonFilter("director-filter")
@Data
public class Director {

    private String nick;
    private String name;
}
