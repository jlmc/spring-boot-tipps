package io.github.jlmc.orders.interfaces.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class KOrdersControllerAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(KOrdersControllerAdvice.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handlerRequestBody(MethodArgumentNotValidException e) {
        List<FieldError> errorList = e.getBindingResult().getFieldErrors();
        String errorMessage;
        errorMessage = errorList.stream()
                                .map(fieldError -> fieldError.getField() + " - " + fieldError.getDefaultMessage())
                                .sorted().collect(Collectors.joining(", "));

        LOGGER.info("errorMessage : {} ", errorMessage);
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }
}
