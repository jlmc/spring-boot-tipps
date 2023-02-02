package io.github.jlmc.sb.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jlmc.sb.validation.advices.ConstraintViolationExceptionHandler;
import io.github.jlmc.sb.validation.advices.MethodArgumentNotValidExceptionHandler;
import io.github.jlmc.sb.validation.advices.WebExchangeBindExceptionHandler;
import jakarta.validation.ConstraintViolationException;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AutoConfiguration
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ConstraintViolationException.class, ObjectMapper.class})
public class ValidationsAutoConfiguration {

    @Bean
    @ConditionalOnClass(ObjectMapper.class)
    @ConditionalOnMissingBean(ConstraintViolationExceptionHandler.class)
    public ConstraintViolationExceptionHandler constraintViolationExceptionHandler(ObjectMapper objectMapper) {
        return new ConstraintViolationExceptionHandler(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean(WebExchangeBindExceptionHandler.class)
    @ConditionalOnClass({ObjectMapper.class, MessageSource.class})
    //@ConditionalOnBean({ObjectMapper.class, MessageSource.class})
    //@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    public WebExchangeBindExceptionHandler webExchangeBindExceptionHandler(ObjectMapper objectMapper, MessageSource messageSource) {
        return new WebExchangeBindExceptionHandler(objectMapper, messageSource);
    }

    @Bean
    @ConditionalOnMissingBean(MethodArgumentNotValidExceptionHandler.class)
    @ConditionalOnClass({ObjectMapper.class, MessageSource.class})
    //@ConditionalOnBean({ObjectMapper.class, MessageSource.class})
    //@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    public MethodArgumentNotValidExceptionHandler MethodArgumentNotValidExceptionHandler(ObjectMapper objectMapper, MessageSource messageSource) {
        return new MethodArgumentNotValidExceptionHandler(objectMapper, messageSource);
    }
}
