package io.github.jlmc.poc.api.exceptions;

import io.github.jlmc.poc.api.exceptions.outputs.ProblemDetailError;
import io.github.jlmc.poc.api.exceptions.outputs.ProblemDetails;
import io.github.jlmc.poc.api.orders.ex.ProductNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionAdvisor extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionAdvisor.class);

    @Autowired
    MessageSource messageSource;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUncaught(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;


        //ProblemType problemType = ProblemType.INTERNAL_SERVER_ERROR;

        String detail = "An unexpected internal system error has occurred. Please try again and if the problem persists contact us.";
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);

        // Importante colocar o printStackTrace (pelo menos por enquanto, que não estamos fazendo logging)
        // para mostrar a stacktrace na console.
        // Se não fizer isso, ficamos sem ver o stacktrace de exceptions que seriam importantes
        // especialmente na fase de desenvolvimento
        //ex.printStackTrace();
        LOGGER.error(ex.getMessage(), ex);

        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }

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

            ProblemDetails.addErrors(problemDetail, problemDetailErrors);

        } else {
            LOGGER.info("No problem details found");
        }

        return super.handleMethodArgumentNotValid(ex, headers, status, request);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Object> handlerProductNotFoundException(ProductNotFoundException ex, WebRequest request) {
        HttpStatus status = HttpStatus.PRECONDITION_FAILED;

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());

        LOGGER.error(ex.getMessage(), ex);

        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }

}
