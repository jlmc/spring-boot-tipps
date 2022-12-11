package io.github.jlmc.orders.domain.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "There is no order with the provider id")
public class OrderNotFoundException extends RuntimeException {
}
