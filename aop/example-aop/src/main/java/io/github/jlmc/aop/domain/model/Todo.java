package io.github.jlmc.aop.domain.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")

public class Todo {

    private Integer id;

    @NotBlank
    private String title;

}
