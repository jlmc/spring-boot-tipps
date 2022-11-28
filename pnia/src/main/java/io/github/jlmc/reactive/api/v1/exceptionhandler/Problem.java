package io.github.jlmc.reactive.api.v1.exceptionhandler;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class Problem {
    private String code;
    private String message;
}
