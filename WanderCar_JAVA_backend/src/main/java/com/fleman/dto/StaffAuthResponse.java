package com.fleman.dto;

public class StaffAuthResponse {
    private String token;
    private StaffDTO staff;

    public StaffAuthResponse() {}
    public StaffAuthResponse(String token, StaffDTO staff) {
        this.token = token;
        this.staff = staff;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public StaffDTO getStaff() { return staff; }
    public void setStaff(StaffDTO staff) { this.staff = staff; }
}
