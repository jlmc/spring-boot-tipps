package io.github.jlmc.aop.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")

public class Todo {

    private Integer id;

    @NotBlank
    private String title;

}
