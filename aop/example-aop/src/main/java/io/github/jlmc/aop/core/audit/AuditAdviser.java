package io.github.jlmc.aop.core.audit;

import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

public class AuditAdviser implements MethodBeforeAdvice {

    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {

    }
}
