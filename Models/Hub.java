package com.fleetmanagement.Models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hub")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Hub {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hub_id")
    private Integer hubId;

    @Column(name = "hub_name", nullable = false, length = 100)
    private String hubName;

    public Integer getHubId() {
		return hubId;
	}

	public void setHubId(Integer hubId) {
		this.hubId = hubId;
	}

	public String getHubName() {
		return hubName;
	}

	public void setHubName(String hubName) {
		this.hubName = hubName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public Integer getStateId() {
		return stateId;
	}

	public void setStateId(Integer stateId) {
		this.stateId = stateId;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "address", length = 255)
    private String address;

    @Column(name = "city_id", nullable = false)
    private Integer cityId;

    @Column(name = "state_id", nullable = false)
    private Integer stateId;

    @Column(name = "pincode", nullable = false, length = 10)
    private String pincode;

    @Column(name = "contact_no", nullable = false, length = 15)
    private String contactNo;

    @Column(name = "email", length = 100)
    private String email;
}