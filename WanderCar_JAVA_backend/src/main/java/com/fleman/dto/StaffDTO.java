package com.fleman.dto;

public class StaffDTO {
    private String username;
    private String hub;
    private Long hubId;

    public StaffDTO() {}
    public StaffDTO(String username, String hub, Long hubId) {
        this.username = username;
        this.hub = hub;
        this.hubId = hubId;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getHub() { return hub; }
    public void setHub(String hub) { this.hub = hub; }
    public Long getHubId() { return hubId; }
    public void setHubId(Long hubId) { this.hubId = hubId; }
}
