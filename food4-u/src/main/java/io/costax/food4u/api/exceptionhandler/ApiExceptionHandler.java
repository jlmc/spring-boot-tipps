package io.costax.food4u.api.exceptionhandler;

import io.costax.food4u.domain.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Clock;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    Clock clock = Clock.systemDefaultZone();

    @ResponseBody
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
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
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<?> illegalArgumentExceptionHandler(IllegalArgumentException e, WebRequest request) {
        /*
          return ResponseEntity
                .badRequest()
                .header("X-reason", e.getMessage())
                .body(e.getMessage());
         */
        return handleExceptionInternal(e, e.getMessage(), toReasonHttpHeaders(e.getMessage()), HttpStatus.BAD_REQUEST, request);
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
        Object b = body;
        if (b == null) {
            b = Problem.builder()
                    .status(status.value())
                    .title(status.getReasonPhrase())
                    .build();
        } else if (b instanceof String) {
            b = Problem.builder()
                    .status(status.value())
                    .title((String) body)
                    .build();
        }

        return super.handleExceptionInternal(ex, b, headers, status, request);
    }
}
