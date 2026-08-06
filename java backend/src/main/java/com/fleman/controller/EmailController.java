/**
 * 
 */
package com.fleman.controller;

/**
 * 
 */

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fleman.service.impl.EmailService;


@RestController
public class EmailController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/api/email/test")
   
    public String sendTestEmail() {

        emailService.sendEmail(
                "teamgryffindor45@gmail.com",   //  email
                "Test Email from WanderCar",
                "Congratulations! Your Email Service is working successfully."
        );

        return "Email sent successfully!";
    }
}