package com.planit.planit.common.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final List<CustomAspectHandler> handlers;

    @Around("@annotation(com.planit.planit.common.aop.LogExecutionTime)")
    public Object applyAspectHandlers(ProceedingJoinPoint joinPoint) throws Throwable {
        for (CustomAspectHandler handler : handlers) {
            if (handler.supports(joinPoint)) {
                return handler.handle(joinPoint);
            }
        }
        return joinPoint.proceed();
    }
}
