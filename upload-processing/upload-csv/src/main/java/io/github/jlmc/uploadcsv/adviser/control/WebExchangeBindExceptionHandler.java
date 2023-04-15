package io.github.jlmc.uploadcsv.adviser.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jlmc.uploadcsv.adviser.entity.ApiError;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;

@Component
public class WebExchangeBindExceptionHandler extends AbstractBindExceptionHandler implements ExceptionHandler<WebExchangeBindException> {

    public WebExchangeBindExceptionHandler(MessageSource messageSource, ObjectMapper mapper) {
        super(messageSource, mapper);
    }

    @Override
    public ApiError handler(WebExchangeBindException exception, String errorCode) {
        return super.handler(exception.getBindingResult(), errorCode);
    }
}
