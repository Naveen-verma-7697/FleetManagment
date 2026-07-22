package com.fleetmanagement.Models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoice_header")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InvoiceHeader {

    public Integer getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(Integer invoiceId) {
		this.invoiceId = invoiceId;
	}

	public LocalDateTime getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(LocalDateTime invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public Integer getBookingId() {
		return bookingId;
	}

	public void setBookingId(Integer bookingId) {
		this.bookingId = bookingId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
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

	public String getVehicleNumber() {
		return vehicleNumber;
	}

	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}

	public String getPickupHub() {
		return pickupHub;
	}

	public void setPickupHub(String pickupHub) {
		this.pickupHub = pickupHub;
	}

	public String getDropHub() {
		return dropHub;
	}

	public void setDropHub(String dropHub) {
		this.dropHub = dropHub;
	}

	public BigDecimal getRentalAmount() {
		return rentalAmount;
	}

	public void setRentalAmount(BigDecimal rentalAmount) {
		this.rentalAmount = rentalAmount;
	}

	public BigDecimal getAddonAmount() {
		return addonAmount;
	}

	public void setAddonAmount(BigDecimal addonAmount) {
		this.addonAmount = addonAmount;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getPaymentReference() {
		return paymentReference;
	}

	public void setPaymentReference(String paymentReference) {
		this.paymentReference = paymentReference;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    private Integer invoiceId;

    @Column(name = "invoice_date")
    private LocalDateTime invoiceDate;

    @Column(name = "booking_id")
    private Integer bookingId;

    @Column(name = "customer_name", length = 100)
    private String customerName;

    @Column(name = "phone", length = 15)
    private String phone;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "vehicle_number", length = 20)
    private String vehicleNumber;

    @Column(name = "pickup_hub", length = 100)
    private String pickupHub;

    @Column(name = "drop_hub", length = 100)
    private String dropHub;

    @Column(name = "rental_amount", precision = 10, scale = 2)
    private BigDecimal rentalAmount;

    @Column(name = "addon_amount", precision = 10, scale = 2)
    private BigDecimal addonAmount;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "payment_reference", length = 100)
    private String paymentReference;
    
}