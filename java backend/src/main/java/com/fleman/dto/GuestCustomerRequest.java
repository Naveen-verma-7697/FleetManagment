package com.fleman.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// Matches exactly what CustomerInfoPage collects for a guest checkout —
// upsertGuestCustomer() on the frontend finds-or-creates a Customer row
// from this so the booking always carries a real customerId.
public class GuestCustomerRequest {
    @NotBlank(message = "First name is required")
    private String firstName;

    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email")
    private String email;

    private String phone;
    private String address1;
    private String zip;
    private String drivingLicenseNo;
    // Same base64-PDF convention as UpdateCustomerRequest - optional, a
    // guest can still complete a booking without uploading anything.
    private String govtIdDocument;
    private String govtIdDocumentName;
    private String govtIdDocumentType;

    public GuestCustomerRequest() {}

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress1() { return address1; }
    public void setAddress1(String address1) { this.address1 = address1; }
    public String getZip() { return zip; }
    public void setZip(String zip) { this.zip = zip; }
    public String getDrivingLicenseNo() { return drivingLicenseNo; }
    public void setDrivingLicenseNo(String drivingLicenseNo) { this.drivingLicenseNo = drivingLicenseNo; }
    public String getGovtIdDocument() { return govtIdDocument; }
    public void setGovtIdDocument(String govtIdDocument) { this.govtIdDocument = govtIdDocument; }
    public String getGovtIdDocumentName() { return govtIdDocumentName; }
    public void setGovtIdDocumentName(String govtIdDocumentName) { this.govtIdDocumentName = govtIdDocumentName; }
    public String getGovtIdDocumentType() { return govtIdDocumentType; }
    public void setGovtIdDocumentType(String govtIdDocumentType) { this.govtIdDocumentType = govtIdDocumentType; }
}
