package io.github.jlmc.reactive.api.exceptionhandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    private final ProblemDetailErrorResponseResolver problemDetailErrorResponseResolver;

    public ControllerExceptionHandler(ProblemDetailErrorResponseResolver problemDetailErrorResponseResolver) {
        this.problemDetailErrorResponseResolver = problemDetailErrorResponseResolver;
    }

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ProblemDetail> handleValidationExceptions(WebExchangeBindException ex) {

        ProblemDetail errorResponse = problemDetailErrorResponseResolver.problemDetailErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid request"
        );

        List<Map<String, String>> errorFields =
                ex.getFieldErrors()
                        .stream()
                        .map(it -> Map.of(
                                "field", it.getField(),
                                "detail", it.getDefaultMessage()))
                        .toList();

        errorResponse.setProperty("errors", errorFields);

        return Mono.just(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ProblemDetail>> handleGeneralException(Exception ex) {

        log.error(ex.getMessage(), ex);

        ProblemDetail errorResponse = problemDetailErrorResponseResolver.problemDetailErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred"
        );

        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
    }
}
