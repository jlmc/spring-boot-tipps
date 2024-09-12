package io.github.jlmc.poc.api.exceptions;

import io.github.jlmc.poc.api.exceptions.outputs.ProblemDetailError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionAdvisor extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionAdvisor.class);

    @Autowired
    MessageSource messageSource;


    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {


        ProblemDetail problemDetail = ex.getBody();
        if (problemDetail != null) {

            var problemDetailErrors = ex.getBindingResult().getAllErrors()
                    .stream()
                    .map(objectError -> {

                        String message = messageSource.getMessage(objectError, LocaleContextHolder.getLocale());
                        String name = objectError.getObjectName();

                        if (objectError instanceof FieldError) {
                            name = ((FieldError) objectError).getField();
                        }

                        return new ProblemDetailError(name, message);
                    })
                    .toList();

            problemDetail.setProperty("errors", problemDetailErrors);
        } else {
            LOGGER.info("No problem details found");
        }

        return super.handleMethodArgumentNotValid(ex, headers, status, request);
    }
}
