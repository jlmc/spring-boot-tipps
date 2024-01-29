package io.github.jlmc.xsgoa.api;

import io.github.jlmc.xsgoa.domain.repositories.CustomerVisionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomerVisionException.class)
    public ResponseEntity<ProblemDetail> handlerCustomerVisionException(CustomerVisionException ex) {

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_EXTENDED);
        problemDetail.setDetail(ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }
}
