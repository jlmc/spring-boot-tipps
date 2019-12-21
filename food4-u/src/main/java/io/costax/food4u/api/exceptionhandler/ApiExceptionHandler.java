package io.costax.food4u.api.exceptionhandler;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import io.costax.food4u.domain.exceptions.BusinessException;
import io.costax.food4u.domain.exceptions.ResourceInUseException;
import io.costax.food4u.domain.exceptions.ResourceNotFoundException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Clock;
import java.util.stream.Collectors;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {
    // The clock instance allows us to have deterministic result when we have tests cases
    Clock clock = Clock.systemDefaultZone();

    /**
     * Customise the handler the syntax error, to have a better response.
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(final HttpMessageNotReadableException ex,
                                                                  final HttpHeaders headers,
                                                                  final HttpStatus status,
                                                                  final WebRequest request) {

        Throwable rootCause = ExceptionUtils.getRootCause(ex);

        if (rootCause instanceof InvalidFormatException) {
            return handleInvalidFormatException((InvalidFormatException) rootCause, headers, status, request);
        }

        ProblemType problemType = ProblemType.MESSAGE_NOT_READABLE;
        String detail = "The request body is invalid. Check syntax error. " + ex.getMessage();
        Problem problem = Problem.createBuilder(status, problemType, detail).build();
        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    private ResponseEntity<Object> handleInvalidFormatException(final InvalidFormatException ex,
                                                                final HttpHeaders headers,
                                                                final HttpStatus status,
                                                                final WebRequest request) {
        String path = ex.getPath().stream()
                .map(JsonMappingException.Reference::getFieldName)
                .collect(Collectors.joining("."));

        ProblemType problemType = ProblemType.MESSAGE_NOT_READABLE;

        String detail = String.format(
                """
                The property '%s' contain the value '%s', witch is of invalid type. a value of the type '%s' should be used.
                """, path, ex.getValue(), ex.getTargetType().getSimpleName());

        Problem problem = Problem.createBuilder(status, problemType, detail).build();
        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    @ResponseBody
    @ExceptionHandler(ResourceNotFoundException.class)
        //@ResponseStatus(HttpStatus.NOT_FOUND)
    ResponseEntity<?> resourceNotFoundHandler(ResourceNotFoundException e, WebRequest request) {
        //return ResponseEntity.notFound().header("X-Reason", e.getMessage()).build();
        HttpStatus status = HttpStatus.NOT_FOUND;
        ProblemType problemType = ProblemType.ENTITY_NOT_FOUND;
        String detail = e.getMessage();
        Problem problem = Problem.createBuilder(status, problemType, detail).build();

        return handleExceptionInternal(e, problem, new HttpHeaders(), status, request);
    }

    @ResponseBody
    @ExceptionHandler(IllegalArgumentException.class)
        //@ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<?> illegalArgumentExceptionHandler(IllegalArgumentException e, WebRequest request) {
        /*
          return ResponseEntity
                .badRequest()
                .header("X-reason", e.getMessage())
                .body(e.getMessage());
         */
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Problem problem = Problem.createBuilder(status, ProblemType.ILLEGAL_ARGUMENT, e.getMessage()).build();

        return handleExceptionInternal(e, problem, new HttpHeaders(), status, request);
    }

    @ResponseBody
    @ExceptionHandler(BusinessException.class)
    ResponseEntity<?> businessExceptionHandler(BusinessException e, WebRequest request) {
        final HttpStatus status = HttpStatus.BAD_REQUEST;
        Problem problem = Problem.createBuilder(status, ProblemType.BUSINESS_ERROR, e.getMessage()).build();

        return handleExceptionInternal(e, problem, new HttpHeaders(), status, request);
    }

    @ResponseBody
    @ExceptionHandler(ResourceInUseException.class)
    ResponseEntity<?> resourceInUseExceptionHandler(ResourceInUseException e, WebRequest request) {
        final HttpStatus status = HttpStatus.CONFLICT;
        Problem problem = Problem.createBuilder(status, ProblemType.RESOURCE_IN_USE, e.getMessage()).build();

        return handleExceptionInternal(e, problem, new HttpHeaders(), status, request);
    }


    private HttpHeaders toReasonHttpHeaders(final String message) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-reason", message);
        return httpHeaders;
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(final Exception ex,
                                                             final Object body,
                                                             final HttpHeaders headers,
                                                             final HttpStatus status,
                                                             final WebRequest request) {
        Object customBody = body;
        if (customBody == null) {
            customBody = Problem.builder()
                    .status(status.value())
                    .title(status.getReasonPhrase())
                    .build();
        } else if (customBody instanceof String) {
            customBody = Problem.builder()
                    .status(status.value())
                    .title((String) body)
                    .build();
        }

        return super.handleExceptionInternal(ex, customBody, headers, status, request);
    }
}
