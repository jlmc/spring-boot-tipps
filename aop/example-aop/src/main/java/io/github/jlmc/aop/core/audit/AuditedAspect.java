package io.github.jlmc.aop.core.audit;

import io.github.jlmc.aop.core.audit.internal.AuditInfo;
import io.github.jlmc.aop.core.audit.internal.AuditNotifier;
import lombok.Data;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Optional;

@Aspect
@Component
public class AuditedAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditedAspect.class);

    private final AuditNotifier auditNotifier;

    public AuditedAspect(AuditNotifier auditNotifier) {
        this.auditNotifier = auditNotifier;
    }

    @Around(value = "@annotation(Audited)")
    public Object handler(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Optional<AuditInfo> auditInfo =
                AuditSupport.getAuditInfo(proceedingJoinPoint);

        try {
            Object result = proceedingJoinPoint.proceed();

            auditInfo.ifPresent(it -> {
                Integer httpResponseStatus =
                        AuditSupport.getHttpResponseStatusCode(result, proceedingJoinPoint);
                it.setHttpResponseStatus(httpResponseStatus);
            });


            return result;
        } catch (Throwable e) {
            auditInfo.ifPresent(it -> it.setProblem(e));
            throw e;
        } finally {

            auditInfo.ifPresent(auditNotifier::register);

        }
    }

}

