package io.github.jlmc.aop.core.audit;

import io.github.jlmc.aop.core.audit.internal.AuditInfo;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Optional;

final class AuditSupport {

    static Object resolveIdentifier(Object[] args, Method method, String idParameterName) {
        if (idParameterName == null || args.length == 0) {
            return null;
        }

        Parameter[] parameters = method.getParameters();
        int identifierIndex = -1;
        for (int i = 0; i < parameters.length; i++) {
            String name = parameters[i].getName();
            if (idParameterName.equals(name)) {
                identifierIndex = i;
                break;
            }
        }

        if (identifierIndex < 0) {
            return null;
        }

        return args[identifierIndex];
    }

    static Optional<AuditInfo> getAuditInfo(ProceedingJoinPoint proceedingJoinPoint) {
        Signature signature = proceedingJoinPoint.getStaticPart().getSignature();

        if (signature instanceof MethodSignature methodSignature) {
            Method method = methodSignature.getMethod();
            Audited audited = method.getAnnotation(Audited.class);
            if (audited != null) {
                Object[] args = proceedingJoinPoint.getArgs();

                return Optional.of(
                        new AuditInfo(
                                audited.operation(),
                                audited.severity().getValue(),
                                AuditSupport.resolveIdentifier(
                                        args,
                                        method,
                                        audited.idParameterName())
                        ));
            }
        }
        return Optional.empty();
    }

    static Integer getHttpResponseStatusCode(Object result, ProceedingJoinPoint proceedingJoinPoint) {
        if (result instanceof ResponseEntity<?> responseEntity) {
            return responseEntity.getStatusCodeValue();
        } else {
            Signature signature = proceedingJoinPoint.getStaticPart().getSignature();
            if (signature instanceof MethodSignature methodSignature) {
                Method method = methodSignature.getMethod();

                ResponseStatus responseStatus = method.getAnnotation(ResponseStatus.class);
                if (responseStatus != null) {
                    return responseStatus.value().value();
                }

                if (Void.class == method.getReturnType()) {
                    return HttpStatus.NO_CONTENT.value();
                }
            }
        }
        return null;
    }
}
