package com.fleman;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.TimeZone;

@SpringBootApplication
@EnableAsync
public class FlemanApplication {
    public static void main(String[] args) {
        // Set before the Spring context starts, so every LocalDateTime.now()
        // call (booking creation, invoice generation, etc.) reflects IST —
        // this is an India-based deployment. Setting it in application.properties
        // alone has no effect on the JVM's actual default timezone.
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
        SpringApplication.run(FlemanApplication.class, args);
    }
}
