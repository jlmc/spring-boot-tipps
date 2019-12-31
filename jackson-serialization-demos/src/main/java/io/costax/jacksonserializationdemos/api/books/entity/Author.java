package io.costax.jacksonserializationdemos.api.books.entity;

import com.fasterxml.jackson.annotation.JsonView;
import io.costax.jacksonserializationdemos.api.books.jackson.Views;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Author {

    @EqualsAndHashCode.Include
    private Integer id;
    @JsonView({Views.BookSummary.class})
    private String name;
}
