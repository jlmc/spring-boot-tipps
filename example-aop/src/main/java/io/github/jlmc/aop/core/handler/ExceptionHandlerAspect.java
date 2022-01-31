package io.github.jlmc.aop.core.handler;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Aspect
@Component
public class ExceptionHandlerAspect {

    @Around(value = "execution(* io.github.jlmc.aop.domain.*.*(..))")
    public Object handler(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        try {
            Object proceed = proceedingJoinPoint.proceed();
            return proceed;
        } catch (MyCustomException e) {
            System.out.println(e.getHttpStatus());
            System.out.println(e.getMessage());

            return new ResponseStatusException(e.getHttpStatus(), e.getMessage());
        }
    }
}
