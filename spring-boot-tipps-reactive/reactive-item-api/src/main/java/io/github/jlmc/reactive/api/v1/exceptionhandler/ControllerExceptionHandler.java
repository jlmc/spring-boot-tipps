package io.github.jlmc.reactive.api.v1.exceptionhandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler  {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handlerUnhatchedException(Exception exception) {
        log.error("Attention: handler Unhatched Exception: {}", exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(Problem.of("123-500", exception.getMessage()));

    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handlerRuntimeException(RuntimeException exception) {
        log.error("Exception caught in handlerRuntimeException : {}", exception.getMessage(), exception );
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Problem.of("123-503", exception.getMessage()));
    }
}
