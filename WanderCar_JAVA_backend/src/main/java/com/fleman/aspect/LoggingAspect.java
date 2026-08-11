/**
 * 
 */
package com.fleman.aspect;

/**
 * 
 */





import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Around("execution(* com.fleman.service.impl.*.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();

        System.out.println("Method Started : "
                + joinPoint.getSignature().getName());

        Object result = joinPoint.proceed();

        long end = System.currentTimeMillis();

        System.out.println("Method Finished : "
                + joinPoint.getSignature().getName());

        System.out.println("Execution Time : "
                + (end - start) + " ms");

        return result;
    }
}