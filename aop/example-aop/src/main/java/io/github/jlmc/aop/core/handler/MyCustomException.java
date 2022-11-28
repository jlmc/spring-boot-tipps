package io.github.jlmc.aop.core.handler;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class MyCustomException extends RuntimeException {

    private final HttpStatus httpStatus;

    public MyCustomException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
