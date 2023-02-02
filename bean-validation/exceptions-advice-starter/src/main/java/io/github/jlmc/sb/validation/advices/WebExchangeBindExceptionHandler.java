package io.github.jlmc.sb.validation.advices;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.support.WebExchangeBindException;

public class WebExchangeBindExceptionHandler extends AbstractBindExceptionHandler implements ValidationAdvice<WebExchangeBindException> {

    public WebExchangeBindExceptionHandler(ObjectMapper mapper, MessageSource messageSource) {
        super(mapper, messageSource);
    }

    @Override
    public Class<WebExchangeBindException> throwableType() {
        return WebExchangeBindException.class;
    }

    @Override
    BindingResult getBindingResult(Throwable throwable) {
        return ((WebExchangeBindException) throwable).getBindingResult();
    }
}
