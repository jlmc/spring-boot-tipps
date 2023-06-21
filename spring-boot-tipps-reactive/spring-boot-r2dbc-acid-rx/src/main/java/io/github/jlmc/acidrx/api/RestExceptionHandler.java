package io.github.jlmc.acidrx.api;

import io.github.jlmc.acidrx.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity resourceNotFound(ResourceNotFoundException ex) {
        LOGGER.debug("handling exception::" + ex);
        return ResponseEntity.notFound().build();
    }


    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity illegalArgument(IllegalArgumentException ex) {
        LOGGER.debug("handling exception::" + ex);
        return ResponseEntity.badRequest().build();
    }


}
