package com.fleman.controller;

import com.fleman.dto.*;
import com.fleman.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final CustomerService customerService;

    public AuthController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(customerService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
    	System.out.println(request.getFullName());
        System.out.println(request.getEmail());
    			return ResponseEntity.ok(customerService.register(request));
    }

    @PostMapping("/staff-login")
    public ResponseEntity<StaffAuthResponse> staffLogin(@Valid @RequestBody StaffLoginRequest request) {
        return ResponseEntity.ok(customerService.staffLogin(request));
    }
}
