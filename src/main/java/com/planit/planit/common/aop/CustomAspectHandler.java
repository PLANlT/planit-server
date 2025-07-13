package com.planit.planit.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;

public interface CustomAspectHandler {
    boolean supports(ProceedingJoinPoint joinPoint);
    Object handle(ProceedingJoinPoint joinPoint) throws Throwable;
}