package com.fleetmanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "customer_master")

public class Customer {

       @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       private Integer customerId;
       
       @Column(nullable = false,length = 100)
       private String customerName;
       
       @Column(length = 150)
       private String address1;
       
       @Column(length = 150)
       private String address2;
       
       @Column(length = 60)
       private String city;
       
       @Column(length = 60)
       private String state;
       
       @Column(length = 10)
       private String pincode;

       @Column(length = 15)
       private String phone;

       @Column(length = 100)
       private String email;

       @Column(length = 20)
       private String passport;
       
       @Column(length = 20)
       private Integer drivinglicense;
       
       @Column
       private Integer age;
       
       public Customer() {
    	   
       }

	   public Integer getCustomerId() {
		   return customerId;
	   }

	   public void setCustomerId(Integer customerId) {
		   this.customerId = customerId;
	   }

	   public String getCustomerName() {
		   return customerName;
	   }

	   public void setCustomerName(String customerName) {
		   this.customerName = customerName;
	   }

	   public String getAddress1() {
		   return address1;
	   }

	   public void setAddress1(String address1) {
		   this.address1 = address1;
	   }

	   public String getAddress2() {
		   return address2;
	   }

	   public void setAddress2(String address2) {
		   this.address2 = address2;
	   }

	   public String getCity() {
		   return city;
	   }

	   public void setCity(String city) {
		   this.city = city;
	   }

	   public String getState() {
		   return state;
	   }

	   public void setState(String state) {
		   this.state = state;
	   }

	   public String getPincode() {
		   return pincode;
	   }

	   public void setPincode(String pincode) {
		   this.pincode = pincode;
	   }

	   public String getPhone() {
		   return phone;
	   }

	   public void setPhone(String phone) {
		   this.phone = phone;
	   }

	   public String getEmail() {
		   return email;
	   }

	   public void setEmail(String email) {
		   this.email = email;
	   }

	   public String getPassport() {
		   return passport;
	   }

	   public void setPassport(String passport) {
		   this.passport = passport;
	   }

	   public Integer getDrivinglicense() {
		   return drivinglicense;
	   }

	   public void setDrivinglicense(Integer drivinglicense) {
		   this.drivinglicense = drivinglicense;
	   }

	   public Integer getAge() {
		   return age;
	   }

	   public void setAge(Integer age) {
		   this.age = age;
	   }
       
       
	
}
