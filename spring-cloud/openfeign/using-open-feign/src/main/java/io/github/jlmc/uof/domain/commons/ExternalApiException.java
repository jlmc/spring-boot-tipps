package io.github.jlmc.uof.domain.commons;

import org.springframework.http.HttpStatus;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ExternalApiException extends RuntimeException {

    private ExternalApiProblemDetail externalApiProblemDetail;

    public ExternalApiException(ExternalApiProblemDetail externalApiProblemDetail) {
        super(externalApiProblemDetail.message());
        this.externalApiProblemDetail = externalApiProblemDetail;
    }

    public ExternalApiProblemDetail getExternalApiProblemDetail() {
        return externalApiProblemDetail;
    }


    public static ExternalApiException of(HttpStatus httpStatus, ExternalApiProblemDetail externalApiProblemDetail) {
        if (HttpStatus.UNAUTHORIZED == httpStatus) {
            return new ExternalApiException.Unauthorized(externalApiProblemDetail);
        }
        if (HttpStatus.FORBIDDEN == httpStatus) {
            return new ExternalApiException.Forbidden(externalApiProblemDetail);
        }
        if (HttpStatus.BAD_REQUEST == httpStatus) {
            return new ExternalApiException.BadRequest(externalApiProblemDetail);
        }
        if (HttpStatus.NOT_FOUND == httpStatus) {
            return new ExternalApiException.NotFound(externalApiProblemDetail);
        }

        return new ExternalApiException(externalApiProblemDetail);
    }

    public static class NotFound extends ExternalApiException {
        public NotFound(ExternalApiProblemDetail externalApiProblemDetail) {
            super(externalApiProblemDetail);
        }
    }

    public static class Unauthorized extends ExternalApiException {
        public Unauthorized(ExternalApiProblemDetail externalApiProblemDetail) {
            super(externalApiProblemDetail);
        }
    }
    public static class BadRequest extends ExternalApiException {
        public BadRequest(ExternalApiProblemDetail externalApiProblemDetail) {
            super(externalApiProblemDetail);
        }
    }

    public static class Forbidden extends ExternalApiException {
        public Forbidden(ExternalApiProblemDetail externalApiProblemDetail) {
            super(externalApiProblemDetail);
        }
    }

    public static class ExternalApiProblemDetail {
        private final String requestMethod;
        private final String requestUrl;
        private final String responseReason;
        private final int responseHttpStatus;
        private final Object responseBody;
        private Map<String, Collection<String>> responseHeaders = new HashMap<>();

        private String message() {
            return "The external request '%s %s' result in a error '%s %s'".formatted(requestMethod, requestUrl, responseHttpStatus, responseReason);
        }

        public String moreDetails() {
            if (responseBody == null) return null;
            return Objects.toString(responseBody);
        }

        public static Builder builder() {
            return new Builder();
        }

        private ExternalApiProblemDetail(Builder builder) {
            this.requestMethod = builder.requestMethod;
            this.requestUrl = builder.requestUrl;
            this.responseReason = builder.responseReason;
            this.responseHttpStatus = builder.responseHttpStatus;
            this.responseBody = builder.responseBody;
            if (builder.responseHeaders != null) {
                this.responseHeaders.putAll(builder.responseHeaders);
            }
        }

        public static class Builder {
            private String requestMethod;
            private String requestUrl;
            private String responseReason;
            private int responseHttpStatus;
            private Map<String, Collection<String>> responseHeaders = new HashMap<>();
            private Object responseBody;

            public Builder requestMethod(String httpRequestMethod) {
                this.requestMethod = httpRequestMethod;
                return this;
            }

            public Builder requestUrl(String httpRequestUrl) {
                this.requestUrl = httpRequestUrl;
                return this;
            }

            public Builder responseReason(String responseReason) {
                this.responseReason = responseReason;
                return this;
            }

            public Builder responseHttpStatus(int responseHttpStatus) {
                this.responseHttpStatus = responseHttpStatus;
                return this;
            }

            public Builder responseBody(Object body) {
                this.responseBody = body;
                return this;
            }

            public Builder responseHeaders(Map<String, Collection<String>> responseHeaders) {
                this.responseHeaders = responseHeaders;
                return this;
            }

            public ExternalApiProblemDetail build() {
                return new ExternalApiProblemDetail(this);
            }
        }
    }
}
