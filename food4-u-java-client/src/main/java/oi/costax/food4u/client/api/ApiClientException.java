package oi.costax.food4u.client.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import oi.costax.food4u.client.model.Problem;
import org.springframework.web.client.RestClientResponseException;

public class ApiClientException extends RuntimeException {

    private Problem problem;

    protected ApiClientException(final String message, final RestClientResponseException cause) {
        super(message, cause);
        this.problem = deserializeProblem(cause);
    }

    private Problem deserializeProblem(final RestClientResponseException cause) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new JavaTimeModule());
        mapper.findAndRegisterModules();

        try {
             return mapper.readValue(cause.getResponseBodyAsString(), Problem.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public Problem getProblem() {
        return problem;
    }
}
