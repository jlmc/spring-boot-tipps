package io.costax.demo.api.exceptionhandler;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;

/**
 * RFC 7807 (Problem Details for HTTP APIs)
 * <p>
 * IETF: Internet Engineering Task Force (IETF) specified the following properties:
 * <p></p>
 * <p>
 * status:
 * Should be the same value of the http status code when the response "exit" from the server.
 * It can exists proxies servers between the client and server, that proxies can change the http header status od the response.
 * So, the idea is the body property keep the original value of the HTTP status.
 * </p>
 * <p></p>
 * <p>
 * type:
 * URI that identifies the type of problem, The API consumers should consult that uri to identify the type of problem
 * and make their most appropriate decisions. The uri may not exist, but if it exists it must be unique.
 * </p>
 * <p></p>
 * <p>
 * title:
 * Short text for humans describing the problem.
 * The Title must be something generic and equals for all the similar error in the system.
 * If we have two types of resources, and for the same resources we have the same error,
 * then the title should be the same for the both, Eg. "Resource in Use".
 * </p>
 * <p></p>
 * <p>
 * detail:
 * more detailed description of the problem, readable to humans.
 * At this point explain exactly what happened and also suggest the problem if it is possible.
 * API consumers should not attempt to automatically interpret information from this 'detail' property to make decisions.
 * </p>
 * <p></p>
 * <p>
 * instance:
 * Optional property.
 * Used to inform a URI that identifies the exact occurrence of the error.
 * </p>
 *
 * <p></p>
 * <hr>
 * More Notes:
 * We can extend this format by adding other properties, for example: timestamp, resourceId, etc...
 * <hr>
 * <p></p>
 * <p>
 * Should we expose internal details of the error such as stacktrace?
 * <p>
 * Yes, adding this information to another property is a design decision.
 * But beware that if the API is consumed by third parties this practice is not recommended.
 * This information do not add any value for the consumers, sensitive data can also be exposing.
 * "describing the problem in the response body is not an API implementation debugging tool"
 * </p>
 *
 * <p></p>
 * <hr>
 * It is not mandatory that all error responses have a body, for example there are quite common HTTP states that are
 * already very well defined (The HTTP 415 Unsupported Media Type) in these cases the response may be empty.
 * <hr>
 *
 * @see <a href="https://tools.ietf.org/html/rfc7807">RFC 7807 (Problem Details for HTTP APIs)</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
//@Builder
public class Problem {
    private Integer status;
    private String type;
    private String title;
    private String detail;
    private Instant timestamp;
    private List<Error> errors;

    private Problem(ProblemBuilder builder) {
        this.status = builder.status;
        this.type = builder.type;
        this.title = builder.title;
        this.detail = builder.detail;
        this.timestamp = builder.timestamp;
        this.errors = builder.errors;
    }

    static Problem.ProblemBuilder builder() {
        return new ProblemBuilder();
    }

    static Problem.ProblemBuilder createBuilder(final HttpStatus status,
                                                final ProblemType problemType,
                                                final String detail,
                                                final Instant instant) {

        return Problem.builder()
                .status(status)
                .type(problemType.getUri())
                .title(problemType.getTitle())
                .detail(detail)
                .timestamp(instant);
    }

    @Getter
    protected static class Error {
        private String name;
        private String userMessage;

        private Error(final String name, final String userMessage) {
            this.name = name;
            this.userMessage = userMessage;
        }

        public static Error of(final String name, final String userMessage) {
            return new Error(name, userMessage);
        }
    }

    public static class ProblemBuilder {
        private Integer status;
        private String type;
        private String title;
        private String detail;

        private Instant timestamp;
        private List<Error> errors;

        public ProblemBuilder status(final HttpStatus status) {
            this.status = status.value();
            return this;
        }

        public ProblemBuilder type(final String type) {
            this.type = type;
            return this;
        }

        public ProblemBuilder title(String title) {
            this.title = title;
            return this;
        }

        public ProblemBuilder detail(String detail) {
            this.detail = detail;
            return this;
        }

        public Problem build() {
            return new Problem(this);
        }

        public ProblemBuilder timestamp(final Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ProblemBuilder errors(final List<Error> errors) {
            this.errors = errors;
            return this;
        }
    }
}
