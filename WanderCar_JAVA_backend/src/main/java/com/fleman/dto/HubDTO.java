package com.fleman.dto;

public class HubDTO {
    private Long hubId;
    private String hubName;
    private String address;
    private Long cityId;
    private Long stateId;
    private String pincode;
    private String contactNo;
    private String email;

    public HubDTO() {}

    public HubDTO(Long hubId, String hubName, String address, Long cityId, Long stateId,
                  String pincode, String contactNo, String email) {
        this.hubId = hubId;
        this.hubName = hubName;
        this.address = address;
        this.cityId = cityId;
        this.stateId = stateId;
        this.pincode = pincode;
        this.contactNo = contactNo;
        this.email = email;
    }

    public Long getHubId() { return hubId; }
    public void setHubId(Long hubId) { this.hubId = hubId; }
    public String getHubName() { return hubName; }
    public void setHubName(String hubName) { this.hubName = hubName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Long getCityId() { return cityId; }
    public void setCityId(Long cityId) { this.cityId = cityId; }
    public Long getStateId() { return stateId; }
    public void setStateId(Long stateId) { this.stateId = stateId; }
    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }
    public String getContactNo() { return contactNo; }
    public void setContactNo(String contactNo) { this.contactNo = contactNo; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
