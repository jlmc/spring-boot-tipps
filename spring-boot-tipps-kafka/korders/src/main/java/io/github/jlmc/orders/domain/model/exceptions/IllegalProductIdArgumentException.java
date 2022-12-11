package io.github.jlmc.orders.domain.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "There is no product with the provider id")
public class IllegalProductIdArgumentException extends RuntimeException {
    public IllegalProductIdArgumentException(String message) {
        super(message);
    }
}
