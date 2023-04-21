package io.costax.idoit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionHandlerMapper extends ResponseEntityExceptionHandler {

    private final MessageSource messageSource;

    private final Function<Exception, String> devMessageMapper = (ex) -> ex.getCause() == null ?
            ex.getMessage() : ex.getCause().getMessage();

    @Autowired
    public ExceptionHandlerMapper(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {

        var errors = createErrors(ex.getBindingResult());

        return handleExceptionInternal(ex, errors, headers, HttpStatus.BAD_REQUEST, request);
    }

    private List<Error> createErrors(final BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .map(fieldError -> new Error(
                        messageSource.getMessage(fieldError, LocaleContextHolder.getLocale()),
                        fieldError.toString())
                ).collect(Collectors.toList());
    }

    public static class Error {

        private final String userMessage;
        private final String devMessage;

        private Error(String userMessage, String devMessage) {
            this.userMessage = userMessage;
            this.devMessage = devMessage;
        }

        public static Error of(String userMessage, String devMessage) {
            return new Error(userMessage, devMessage);
        }

        public String getUserMessage() {
            return userMessage;
        }

        public String getDevMessage() {
            return devMessage;
        }
    }
}
