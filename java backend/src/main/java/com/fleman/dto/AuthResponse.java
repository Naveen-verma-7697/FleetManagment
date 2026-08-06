package com.fleman.dto;

public class AuthResponse {
    private String token;
    private CustomerDTO customer;

    public AuthResponse() {}
    public AuthResponse(String token, CustomerDTO customer) {
        this.token = token;
        this.customer = customer;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public CustomerDTO getCustomer() { return customer; }
    public void setCustomer(CustomerDTO customer) { this.customer = customer; }
}
