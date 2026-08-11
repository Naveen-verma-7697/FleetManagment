package com.fleman.dto;

import java.time.LocalDate;

// A partial patch — any null field here is left unchanged on the existing
// Customer row (see CustomerServiceImpl.updateCustomer).
public class UpdateCustomerRequest {
    private Long customerId;
    private String fullName;
    private String phone;
    private LocalDate dateOfBirth;
    private String drivingLicenseNo;
    private String passportNo;
    private String address1;
    private String address2;
    private Long cityId;
    private Long stateId;
    private String pincode;
    // Base64-encoded PDF, sent from the "your details" / profile-edit form's
    // file input. Null/omitted leaves whatever is already on file untouched
    // (same partial-patch convention as every other field on this DTO).
    private String govtIdDocument;
    private String govtIdDocumentName;
    private String govtIdDocumentType;

    public UpdateCustomerRequest() {}

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
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
    public String getGovtIdDocument() { return govtIdDocument; }
    public void setGovtIdDocument(String govtIdDocument) { this.govtIdDocument = govtIdDocument; }
    public String getGovtIdDocumentName() { return govtIdDocumentName; }
    public void setGovtIdDocumentName(String govtIdDocumentName) { this.govtIdDocumentName = govtIdDocumentName; }
    public String getGovtIdDocumentType() { return govtIdDocumentType; }
    public void setGovtIdDocumentType(String govtIdDocumentType) { this.govtIdDocumentType = govtIdDocumentType; }
}
