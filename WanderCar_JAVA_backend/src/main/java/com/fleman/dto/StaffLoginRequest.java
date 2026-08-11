package com.fleman.dto;

import jakarta.validation.constraints.NotBlank;

public class StaffLoginRequest {
    @NotBlank(message = "Staff username is required")
    private String username;

    @NotBlank(message = "Staff password is required")
    private String password;

    public StaffLoginRequest() {}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
