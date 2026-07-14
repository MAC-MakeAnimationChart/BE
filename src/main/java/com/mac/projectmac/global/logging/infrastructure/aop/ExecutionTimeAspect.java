package com.mac.projectmac.global.logging.infrastructure.aop;

import com.mac.projectmac.global.logging.config.LoggingProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ExecutionTimeAspect {

    private final LoggingProperties loggingProperties;

    @Around("execution(* com.mac.projectmac.*..*Controller.*(..))")
    public Object measureControllerTime(ProceedingJoinPoint joinPoint) throws Throwable {
        return measure(joinPoint, loggingProperties.getSlowMethod().getControllerThresholdMs(), "Controller");
    }

    @Around("execution(* com.mac.projectmac.*..*Service.*(..))")
    public Object measureServiceTime(ProceedingJoinPoint joinPoint) throws Throwable {
        return measure(joinPoint, loggingProperties.getSlowMethod().getServiceThresholdMs(), "Service");
    }

    private Object measure(ProceedingJoinPoint joinPoint, long thresholdMs, String layer) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long elapsed = System.currentTimeMillis() - start;
            String className = joinPoint.getTarget().getClass().getSimpleName();
            String methodName = joinPoint.getSignature().getName();

            if (elapsed >= thresholdMs) {
                log.warn("[ExecutionTimeAspect] {} 슬로우 감지 - {}.{}() 실행시간={}ms (기준={}ms)",
                        layer, className, methodName, elapsed, thresholdMs);
            }
        }
    }
}
