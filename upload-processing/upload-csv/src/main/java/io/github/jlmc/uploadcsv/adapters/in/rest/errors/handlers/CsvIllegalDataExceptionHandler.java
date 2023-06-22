package io.github.jlmc.uploadcsv.adapters.in.rest.errors.handlers;

import io.github.jlmc.uploadcsv.adapters.in.rest.csv.CsvIllegalDataException;
import io.github.jlmc.uploadcsv.adapters.in.rest.errors.models.ApiError;
import io.github.jlmc.uploadcsv.adapters.in.rest.errors.models.ErrorField;
import org.springframework.stereotype.Component;

@Component
public class CsvIllegalDataExceptionHandler implements ExceptionHandler<CsvIllegalDataException> {
    @Override
    public ApiError handler(CsvIllegalDataException exception, String code) {
        return new ApiError(code,
                "Invalid csv input.",
                exception.getError()
                         .map(it -> new ErrorField(it.name(), it.description()))
                         .stream()
                         .toList());
    }
}
