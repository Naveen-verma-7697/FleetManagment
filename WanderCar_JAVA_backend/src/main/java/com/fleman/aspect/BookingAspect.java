/**
 * 
 */
package com.fleman.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * 
 */


@Aspect
@Component
public class BookingAspect {

    @Before("execution(* com.fleman.service.impl.BookingServiceImpl.*(..))")
    public void beforeBooking() {
        System.out.println("Booking Process Started...");
    }

    @AfterReturning("execution(* com.fleman.service.impl.BookingServiceImpl.*(..))")
    public void afterBooking() {
        System.out.println("Booking Process Completed.");
    }
}