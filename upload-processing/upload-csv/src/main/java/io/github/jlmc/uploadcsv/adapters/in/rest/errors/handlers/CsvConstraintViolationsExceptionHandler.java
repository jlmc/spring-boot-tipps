package io.github.jlmc.uploadcsv.adapters.in.rest.errors.handlers;

import io.github.jlmc.uploadcsv.adapters.in.rest.csv.CsvConstraintViolationsException;
import io.github.jlmc.uploadcsv.adapters.in.rest.errors.models.ApiError;
import io.github.jlmc.uploadcsv.adapters.in.rest.errors.models.ErrorField;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CsvConstraintViolationsExceptionHandler implements ExceptionHandler<CsvConstraintViolationsException> {

    @Override
    public ApiError handler(CsvConstraintViolationsException exception, String code) {
        String message = Optional.ofNullable(exception.getMessage()).orElse("The Locations input data contain violation(s)");
        List<ErrorField> errorFields =
                exception.getViolations().stream()
                         .map(it -> new ErrorField("Line " + it.lineNumber(), it.message()))
                         .toList();
        return new ApiError(code, message, errorFields);
    }
}
