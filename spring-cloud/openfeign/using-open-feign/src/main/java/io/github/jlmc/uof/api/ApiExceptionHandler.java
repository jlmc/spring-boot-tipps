package io.github.jlmc.uof.api;

import io.github.jlmc.uof.domain.commons.ExternalApiException;
import io.github.jlmc.uof.domain.commons.NoFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(NoFoundException.class)
    public ResponseEntity<Object> handlerNoFoundException(NoFoundException ex, WebRequest request) {
        ProblemDetail problemDetail = createProblemDetail(ex, HttpStatus.NOT_FOUND, ex.getMessage(), null, null, request);

        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<Object> handlerExternalApiException(ExternalApiException ex, WebRequest request) {
        ExternalApiException.ExternalApiProblemDetail externalApiProblemDetail = ex.getExternalApiProblemDetail();

        ProblemDetail problemDetail = createProblemDetail(ex, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), "external-api-error", null, request);
        problemDetail.setProperty("errorDetail", externalApiProblemDetail.moreDetails());

        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

}
