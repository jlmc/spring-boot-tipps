package io.github.jlmc.sb.validation.advices;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

public class MethodArgumentNotValidExceptionHandler extends AbstractBindExceptionHandler implements ValidationAdvice<MethodArgumentNotValidException> {

    public MethodArgumentNotValidExceptionHandler(ObjectMapper objectMapper, MessageSource messageSource) {
        super(objectMapper, messageSource);
    }

    @Override
    BindingResult getBindingResult(Throwable throwable) {
        return ((MethodArgumentNotValidException) throwable).getBindingResult();
    }

    @Override
    public Class<MethodArgumentNotValidException> throwableType() {
        return MethodArgumentNotValidException.class;
    }

}
