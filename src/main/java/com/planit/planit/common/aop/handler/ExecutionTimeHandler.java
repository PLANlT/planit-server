package com.planit.planit.common.aop.handler;

import com.planit.planit.common.aop.CustomAspectHandler;
import com.planit.planit.common.aop.LogExecutionTime;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class ExecutionTimeHandler implements CustomAspectHandler {

    @Override
    public boolean supports(ProceedingJoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        return method.isAnnotationPresent(LogExecutionTime.class);
    }

    @Override
    public Object handle(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long end = System.currentTimeMillis();

        log.info("[ExecutionTime] {} executed in {}ms", joinPoint.getSignature(), (end - start));
        return result;
    }
}