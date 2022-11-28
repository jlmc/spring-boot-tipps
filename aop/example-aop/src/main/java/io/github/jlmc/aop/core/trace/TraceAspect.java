package io.github.jlmc.aop.core.trace;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TraceAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraceAspect.class);

    @Before(value = "execution(* io.github.jlmc.aop.api.*.*(..))")
    public void logStatementBefore(JoinPoint joinPoint) {
        LOGGER.info("Executing: " + joinPoint);
    }


}
