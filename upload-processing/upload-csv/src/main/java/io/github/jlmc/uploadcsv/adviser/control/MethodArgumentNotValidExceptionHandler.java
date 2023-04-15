package io.github.jlmc.uploadcsv.adviser.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jlmc.uploadcsv.adviser.entity.ApiError;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Component
public class MethodArgumentNotValidExceptionHandler extends AbstractBindExceptionHandler implements ExceptionHandler<MethodArgumentNotValidException> {

    public MethodArgumentNotValidExceptionHandler(MessageSource messageSource, ObjectMapper mapper) {
        super(messageSource, mapper);
    }

    @Override
    public ApiError handler(MethodArgumentNotValidException exception, String errorCode) {
        return super.handler(exception, errorCode);
    }
}
