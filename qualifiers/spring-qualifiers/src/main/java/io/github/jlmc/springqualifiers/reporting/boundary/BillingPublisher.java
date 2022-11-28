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
public class BillingPublisher {

    @Autowired
    @Qualifier("billing")
    EventPublisher billingEvents;

    @Around(value = "@annotation(Billing)")
    public Object handler(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object proceed = proceedingJoinPoint.proceed();

        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getStaticPart().getSignature();
        Method method = signature.getMethod();
        Billing billing = method.getAnnotation(Billing.class);

        billingEvents.publisher(billing.operation());

        return proceed;
    }
}
