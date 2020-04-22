package com.cheapflights.tickets.config;

import lombok.extern.java.Log;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.Arrays;


@Aspect
@Log
@Configuration
@EnableAspectJAutoProxy
public class LoggingAspect {

    @Pointcut("within(com.cheapflights.tickets.controller..*) || within(@org.springframework.web.bind.annotation.RestController *)")
    public void loggingPointcut() {
    }

    @AfterThrowing(pointcut = "loggingPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        log.info(String.format("Exception in %s.%s() with cause = %s", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(), e.getCause()));
    }

    @Around("loggingPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info(String.format("Enter: %s.%s() with argument[s] = %s", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs())));
        Object result = joinPoint.proceed();
        log.info(String.format("Exit: %s.%s() with result = %s", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), result));
        return result;
    }
}

