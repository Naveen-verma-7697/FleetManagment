package com.fleman.dto;

import java.time.LocalDate;

// Never carries the password hash — this is what login/register/getById
// all return to the browser.
public class CustomerDTO {
    private Long customerId;
    private String fullName;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private String drivingLicenseNo;
    private String passportNo;
    private String address1;
    private String address2;
    private Long cityId;
    private Long stateId;
    private String pincode;
    private String status;
    // The full base64 document is deliberately NOT here - it would bloat
    // every customer read. Just enough for the UI to show "license.pdf is
    // on file" and offer a download; see GET /api/customers/{id}/govt-id
    // for the actual content.
    
    private String govtIdDocumentName;
    private String govtIdDocumentType;

    public CustomerDTO() {}

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getDrivingLicenseNo() { return drivingLicenseNo; }
    public void setDrivingLicenseNo(String drivingLicenseNo) { this.drivingLicenseNo = drivingLicenseNo; }
    public String getPassportNo() { return passportNo; }
    public void setPassportNo(String passportNo) { this.passportNo = passportNo; }
    public String getAddress1() { return address1; }
    public void setAddress1(String address1) { this.address1 = address1; }
    public String getAddress2() { return address2; }
    public void setAddress2(String address2) { this.address2 = address2; }
    public Long getCityId() { return cityId; }
    public void setCityId(Long cityId) { this.cityId = cityId; }
    public Long getStateId() { return stateId; }
    public void setStateId(Long stateId) { this.stateId = stateId; }
    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getGovtIdDocumentName() { return govtIdDocumentName; }
    public void setGovtIdDocumentName(String govtIdDocumentName) { this.govtIdDocumentName = govtIdDocumentName; }
    public String getGovtIdDocumentType() { return govtIdDocumentType; }
    public void setGovtIdDocumentType(String govtIdDocumentType) { this.govtIdDocumentType = govtIdDocumentType; }
}
