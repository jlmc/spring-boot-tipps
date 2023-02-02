package io.github.jlmc.sb.app.webflux.rest;

import io.github.jlmc.sb.validation.advices.ValidationAdvice;
import io.github.jlmc.sb.validation.errors.ApiError;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static io.github.jlmc.sb.app.webflux.rest.ErrorCodes.X_400;
import static io.github.jlmc.sb.app.webflux.rest.ErrorCodes.X_500;


@RestControllerAdvice
public class ControllerExceptionAdvices {

    private static final ApiError INTERNAL_SERVER_ERROR = new ApiError(500, X_500, "unexpected error happens", Collections.emptyList());

    @Lazy
    @Autowired(required = false)
    List<ValidationAdvice<? extends Exception>> validationAdvices = List.of();

    @ExceptionHandler({WebExchangeBindException.class, ConstraintViolationException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<ApiError> validationExceptionHandle(Exception e) {
        return badRequestExceptionHandler(e);
    }

    private ResponseEntity<ApiError> badRequestExceptionHandler(Exception e) {
        Optional<ValidationAdvice<? extends Exception>> validationAdvice =
                validationAdvices.stream()
                                 .filter(it -> it.throwableType() == e.getClass())
                                 .findFirst();

        ApiError apiError =
                validationAdvice.map(it -> it.handler(e, X_400))
                                .orElse(INTERNAL_SERVER_ERROR);

        return ResponseEntity.status(apiError.httpCode()).body(apiError);
    }
}
