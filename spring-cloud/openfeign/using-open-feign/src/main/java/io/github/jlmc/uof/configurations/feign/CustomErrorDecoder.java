package io.github.jlmc.uof.configurations.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import io.github.jlmc.uof.domain.commons.ExternalApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

public class CustomErrorDecoder implements ErrorDecoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomErrorDecoder.class);

    private final ObjectMapper objectMapper;

    public CustomErrorDecoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus httpStatus = HttpStatus.valueOf(response.status());

        var externalApiProblemDetail =
                ExternalApiException.ExternalApiProblemDetail.builder()
                        .requestMethod(response.request().httpMethod().name())
                        .requestUrl(response.request().url())
                        .responseHeaders(response.headers())
                        .responseReason(response.reason())
                        .responseHttpStatus(httpStatus.value())
                        .responseHeaders(response.headers())
                        .responseBody(getBody(response))
                        .build();

        return ExternalApiException.of(httpStatus, externalApiProblemDetail);
    }

    private String getBody(Response response) {

        try {
            if (response == null || response.body() == null) return null;

            Reader reader = response.body().asReader(StandardCharsets.UTF_8);

            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuilder sb = new StringBuilder();
            for (String line = null; (line = bufferedReader.readLine()) != null; ) {
                sb.append(line);
            }

            return sb.toString();

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
