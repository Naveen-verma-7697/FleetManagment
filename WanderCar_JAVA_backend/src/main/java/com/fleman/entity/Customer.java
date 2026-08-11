package com.fleman.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

// cityId/stateId are plain logical-reference columns — no @ManyToOne.
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(unique = true, length = 15)
    private String phone;
    private LocalDate dateOfBirth;

    @Column(unique = true, length = 30)
    private String drivingLicenseNo;

    @Column(length = 30)
    private String passportNo;

    @Column(length = 255)
    private String address1;

    @Column(length = 255)
    private String address2;

    private Long cityId;
    private Long stateId;

    @Column(length = 10)
    private String pincode;

    @Enumerated(EnumType.STRING)
    private CustomerStatus status;

    // BCrypt hash — never returned in any DTO. Nullable: guest customers
    // created mid-booking (upsertGuestCustomer) have no password at all.
    @Column(name = "password_hash")
    private String passwordHash;
    
    
    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    @Column(length = 100)
    private String providerId;

    private Boolean emailVerified;

    // Government ID upload (driving licence / Aadhaar / PAN / passport) from
    // the "your details" step. Stored as a base64-encoded string rather than
    // on disk or in object storage — there's no file-storage infra in this
    // project, and @Lob keeps this out of every normal SELECT unless the
    // JPA provider decides otherwise, same tradeoff as any inline-BLOB design.
    @Lob
    @Column(name = "govt_id_document")
    private String govtIdDocument;

    @Column(name = "govt_id_document_name", length = 255)
    private String govtIdDocumentName;

    @Column(name = "govt_id_document_type", length = 30)
    private String govtIdDocumentType;

    public Customer() {}

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
    public CustomerStatus getStatus() { return status; }
    public void setStatus(CustomerStatus status) { this.status = status; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getGovtIdDocument() { return govtIdDocument; }
    public void setGovtIdDocument(String govtIdDocument) { this.govtIdDocument = govtIdDocument; }
    public String getGovtIdDocumentName() { return govtIdDocumentName; }
    public void setGovtIdDocumentName(String govtIdDocumentName) { this.govtIdDocumentName = govtIdDocumentName; }
    public String getGovtIdDocumentType() { return govtIdDocumentType; }
    public void setGovtIdDocumentType(String govtIdDocumentType) { this.govtIdDocumentType = govtIdDocumentType; }
    public AuthProvider getProvider() {return provider;}
    public void setProvider(AuthProvider provider) {this.provider = provider;}
    public String getProviderId() { return providerId;}
    public void setProviderId(String providerId) {this.providerId = providerId;}
    public Boolean getEmailVerified() {  return emailVerified;}
    public void setEmailVerified(Boolean emailVerified) { this.emailVerified = emailVerified;}
    
    public enum AuthProvider {
        LOCAL,
        GOOGLE
    }
    public enum CustomerStatus { ACTIVE, INACTIVE }
}
