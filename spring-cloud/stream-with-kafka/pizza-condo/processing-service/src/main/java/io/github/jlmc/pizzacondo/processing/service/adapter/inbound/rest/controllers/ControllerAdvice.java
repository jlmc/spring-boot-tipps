package io.github.jlmc.pizzacondo.processing.service.adapter.inbound.rest.controllers;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice extends ResponseEntityExceptionHandler {

    /**
     * handler all the exceptions not excepted.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUncaught(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        String detail = "An unexpected internal system error has occurred. Please try again and if the problem persists contact us.";
        problemDetail.setDetail(detail);

        logger.error(ex.getMessage(), ex);

        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }

    @Nullable
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  @Nullable HttpHeaders headers,
                                                                  @Nullable HttpStatusCode status,
                                                                  @Nullable WebRequest request) {
        ProblemDetail problemDetail = ex.updateAndGetBody(getMessageSource(), LocaleContextHolder.getLocale());

        List<ProblemDetailError> errors =
                ex.getBindingResult().getAllErrors()
                        .stream()
                        .map(objectError -> {
                            String errorMessage = objectError.getDefaultMessage();
                            String name = objectError.getObjectName();
                            if (objectError instanceof FieldError fe) {
                                name = fe.getField();
                            }
                            return new ProblemDetailError(name, errorMessage);
                        })
                        .toList();

        problemDetail.setProperty("errors", errors);

        return this.handleExceptionInternal(ex, problemDetail, headers, status, request);
    }

    private final record ProblemDetailError(String field, String message) {
    }
}
