package io.github.jlmc.aop.core.log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Aspect
@Component
public class LogExecutionTimeAspect {

    @Around("@annotation(LogExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        //this.doSameThing(joinPoint);

        Object[] args = joinPoint.getArgs();
        Object proceed = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - start;

        System.out.println(joinPoint.getSignature() + " executed in " + executionTime + "ms");
        return proceed;
    }

    private void doSameThing(ProceedingJoinPoint pjp) {
        Signature signature = pjp.getSignature();

        String methodName = pjp.getSignature().getName();

        Class declaringType = pjp.getSignature().getDeclaringType();

        if (signature instanceof MethodSignature methodSignature) {
            Method method = methodSignature.getMethod();
            Class returnType = methodSignature.getReturnType();

            Annotation[] annotations = method.getAnnotations();

            Parameter[] parameters = method.getParameters();

            for (Parameter parameter : parameters) {
                Class<?> parameterType = parameter.getType();
                Object[] args = pjp.getArgs();

            }

        }


    }

    // @Before("execution(* EmployeeManager.getEmployeeById(..))")
    // @AfterReturning("com.xyz.myapp.SystemArchitecture.dataAccessOperation()")
    // @AfterThrowing(
    //    pointcut="com.xyz.myapp.SystemArchitecture.dataAccessOperation()",
    //    throwing="ex")
    //  @After("com.xyz.myapp.SystemArchitecture.dataAccessOperation()")
}
