package io.github.jlmc.poc.configurations.openfeign;

import feign.FeignException;
import feign.Request;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;

/**
 * By default, only ioexception make the retry process trigger,
 * this implementation makes them tack some server exceptions to get the retry process.
 */
public class CustomErrorDecoder implements ErrorDecoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomErrorDecoder.class);

    private final ErrorDecoder defaultErrorDecode = new ErrorDecoder.Default();

    private static FeignException errorRetryExecuting(int status, Request request, Exception cause) {
        final Long nonRetryable = null;
        return new RetryableException(
                status,
                format("%s executing %s %s", cause.getMessage(), request.httpMethod(), request.url()),
                request.httpMethod(),
                cause,
                nonRetryable,
                request);
    }

    private static boolean isServerError(int status) {
        return status >= 500 && status <= 599;
    }

    /**
     * By default, only ioexception make the retry process trigger,
     * this implementation makes them tack some server exceptions to get the retry process.
     *
     * @param methodKey {@link feign.Feign#configKey} of the java method that invoked the request. ex.
     *                  {@code IAM#getUser()}
     * @param response  HTTP response where {@link Response#status() status} is greater than or equal
     *                  to {@code 300}.
     */
    @Override
    public Exception decode(String methodKey, Response response) {

        Exception exception = defaultErrorDecode.decode(methodKey, response);

        if (isOneOfCustomRetryableHandledStates(response)) {
            // 501 Not Implemented
            // 502 Bad Gateway
            // 503 Service Unavailable
            // 504 Gateway Timeout

            LOGGER.error("*****************> START of Retry *****> Feign status: {} ***> Feign status: {}", response.status(), response.reason());

            if (isServerError(response.status())) {
                LOGGER.debug("Is one server error...");
            }

            return errorRetryExecuting(response.status(), response.request(), exception);
        }

        return exception;
    }

    private boolean isOneOfCustomRetryableHandledStates(Response response) {
        return 502 == response.status() || 503 == response.status() || 504 == response.status();
    }

}
