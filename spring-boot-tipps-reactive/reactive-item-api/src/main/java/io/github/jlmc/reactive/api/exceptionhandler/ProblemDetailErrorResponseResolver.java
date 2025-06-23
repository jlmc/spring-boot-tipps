package io.github.jlmc.reactive.api.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class ProblemDetailErrorResponseResolver {

    public ProblemDetail problemDetailErrorResponse(HttpStatus status, String message) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, message);
        // Optionally you can add more fields
        problemDetail.setType(URI.create("https://yourdomain.com/errors/" + status.value()));
        problemDetail.setTitle(status.getReasonPhrase());
        return problemDetail;
    }
}
