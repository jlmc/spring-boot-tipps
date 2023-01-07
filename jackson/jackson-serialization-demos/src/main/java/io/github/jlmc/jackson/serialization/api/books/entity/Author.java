package io.github.jlmc.jackson.serialization.api.books.entity;

import com.fasterxml.jackson.annotation.JsonView;
import io.github.jlmc.jackson.serialization.api.books.jackson.Views;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class Author {

    @EqualsAndHashCode.Include
    private Integer id;
    @JsonView({Views.BookSummary.class})
    private String name;
}
