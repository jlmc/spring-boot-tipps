package io.github.jlmc.reactive.api.v1.exceptionhandler;

import io.github.jlmc.reactive.domain.services.blacklist.ForbiddenInputValuesException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebInputException;

import jakarta.validation.ConstraintViolationException;

import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handlerUnhatchedException(Exception exception) {
        log.error("Attention: handler Unhatched Exception: {}", exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Problem.of("123-500", exception.getMessage()));

    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handlerRuntimeException(RuntimeException exception) {
        log.error("Exception caught in handlerRuntimeException : {}", exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Problem.of("500", exception.getMessage()));
    }

    @ExceptionHandler({ConstraintViolationException.class, ServerWebInputException.class})
    public ResponseEntity<?> handlerValidationExceptions(Exception exception) {
        log.error("Exception caught in handlerValidationExceptions : {}", exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Problem.of("400", exception.getMessage()));
    }

    @ExceptionHandler({ForbiddenInputValuesException.class})
    public ResponseEntity<?> handlerForbiddenInputValuesException(ForbiddenInputValuesException exception) {
        log.error("Exception caught in handlerForbiddenInputValuesException : {}", exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Problem.of("400", "Unaccepted input values " + exception.getInvalidValues().stream().collect(Collectors.joining(",", "[", "]"))));
    }
}
