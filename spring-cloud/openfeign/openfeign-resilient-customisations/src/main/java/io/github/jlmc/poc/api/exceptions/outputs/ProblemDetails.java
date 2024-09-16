package io.github.jlmc.poc.api.exceptions.outputs;

import org.springframework.http.ProblemDetail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public final class ProblemDetails {

    private static final String ERRORS = "errors";

    public static ProblemDetail addErrors(ProblemDetail problemDetail, Collection<ProblemDetailError> problemDetailErrors) {
        if (problemDetail == null) {
            throw new IllegalArgumentException("problemDetail cannot be null");
        }

        List<ProblemDetailError> errors = concatWithExistingErrors(problemDetail, problemDetailErrors);

        problemDetail.setProperty(ERRORS, errors);

        return problemDetail;
    }

    private static List<ProblemDetailError> concatWithExistingErrors(ProblemDetail problemDetail, Collection<ProblemDetailError> problemDetailErrors) {
        if (problemDetail.getProperties() == null) {
            problemDetail.setProperties(new HashMap<>());
        }

        List<ProblemDetailError> errors = new ArrayList<>();

        if (problemDetail.getProperties().containsKey(ERRORS)) {
            List<ProblemDetailError> preExistingProblemDetails = getProblemDetailErrors(problemDetail);
            errors.addAll(preExistingProblemDetails);
        }

        errors.addAll(problemDetailErrors);

        return List.copyOf(errors);
    }

    public static List<ProblemDetailError> getProblemDetailErrors(ProblemDetail problemDetail) {
        if (problemDetail == null || problemDetail.getProperties() == null) {
            return List.of();
        }

        //noinspection unchecked
        return (List<ProblemDetailError>) problemDetail.getProperties().get(ERRORS);
    }
}
