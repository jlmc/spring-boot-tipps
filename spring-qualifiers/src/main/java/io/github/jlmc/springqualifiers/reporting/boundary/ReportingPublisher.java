package io.github.jlmc.springqualifiers.reporting.boundary;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class ReportingPublisher {

    @Autowired
    @Qualifier("reporting")
    EventPublisher reportingEvents;

    @Around(value = "@annotation(Report)")
    public Object handler(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object proceed = proceedingJoinPoint.proceed();

        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getStaticPart().getSignature();
        Method method = signature.getMethod();
        Report billing = method.getAnnotation(Report.class);

        reportingEvents.publisher(billing.operation());

        return proceed;
    }
}
