package io.github.jlmc.uploadcsv.adviser.boundary;

import io.github.jlmc.uploadcsv.adviser.control.ExceptionHandler;
import io.github.jlmc.uploadcsv.adviser.entity.ApiError;
import io.github.jlmc.uploadcsv.adviser.entity.ErrorField;
import io.github.jlmc.uploadcsv.csv.boundary.CsvIllegalDataException;
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
