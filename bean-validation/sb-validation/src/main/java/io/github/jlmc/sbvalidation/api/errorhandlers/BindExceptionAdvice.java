package io.github.jlmc.sbvalidation.api.errorhandlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.regex.Pattern;

@RestControllerAdvice
public class BindExceptionAdvice {

    @Autowired
    Clock clock;

    @Autowired
    MessageSource messageSource;

    @ExceptionHandler({WebExchangeBindException.class}) // WebExchangeBindException
    public ResponseEntity<Problem> webExchangeBindExceptionHandler(
            WebExchangeBindException th,
            ServerHttpRequest request) {

        return bindingExceptionHandler(th.getBindingResult(), request);
    }

    private ResponseEntity<Problem> bindingExceptionHandler(BindingResult bindingResult, ServerHttpRequest request) {
        List<Problem.Field> fields = toErrorFields(bindingResult);
        Problem problem = Problem.createProblem(
                Instant.now(clock),
                request.getPath().value(),
                400,
                "Bad request",
                "Request cannot be processed due to validation errors")
                                 .setFields(fields);
        return ResponseEntity.badRequest().body(problem);
    }


    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<Problem> methodArgumentNotValidException(MethodArgumentNotValidException e, ServerHttpRequest request) {
        return bindingExceptionHandler(e.getBindingResult(), request);
    }


    private List<Problem.Field> toErrorFields(BindingResult bindingResult) {
        return bindingResult.getAllErrors().stream()
                            .map((ObjectError objectError) -> {

                                String name = "";
                                if (objectError instanceof FieldError fieldError) {
                                    name = fieldError.getField();
                                    Object rejectedValue = fieldError.getRejectedValue();
                                } else {
                                    name = objectError.getObjectName();
                                }

                                String message = messageSource.getMessage(objectError, LocaleContextHolder.getLocale());
                                return new Problem.Field(camelToSnakeCase(name), message);

                            })
                            .toList();
    }


    private static final String CAMEL_REGEX = "(?<=[a-zA-Z])[A-Z]";

    private static String camelToSnakeCase(String txt) {
        String replacement = "$1_$2";
        return txt.replaceAll(CAMEL_REGEX, replacement).toLowerCase();
    }
}
