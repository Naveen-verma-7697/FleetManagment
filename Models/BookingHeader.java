package com.fleetmanagement.Models;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking_header")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookingHeader {

    public Integer getBookingId() {
		return bookingId;
	}

	public void setBookingId(Integer bookingId) {
		this.bookingId = bookingId;
	}

	public LocalDateTime getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(LocalDateTime bookingDate) {
		this.bookingDate = bookingDate;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public Integer getCarId() {
		return carId;
	}

	public void setCarId(Integer carId) {
		this.carId = carId;
	}

	public Integer getPickupHubId() {
		return pickupHubId;
	}

	public void setPickupHubId(Integer pickupHubId) {
		this.pickupHubId = pickupHubId;
	}

	public Integer getDropHubId() {
		return dropHubId;
	}

	public void setDropHubId(Integer dropHubId) {
		this.dropHubId = dropHubId;
	}

	public LocalDateTime getPickupDatetime() {
		return pickupDatetime;
	}

	public void setPickupDatetime(LocalDateTime pickupDatetime) {
		this.pickupDatetime = pickupDatetime;
	}

	public LocalDateTime getReturnDatetime() {
		return returnDatetime;
	}

	public void setReturnDatetime(LocalDateTime returnDatetime) {
		this.returnDatetime = returnDatetime;
	}

	public BookingStatus getBookingStatus() {
		return bookingStatus;
	}

	public void setBookingStatus(BookingStatus bookingStatus) {
		this.bookingStatus = bookingStatus;
	}

	public BigDecimal getEstimatedAmount() {
		return estimatedAmount;
	}

	public void setEstimatedAmount(BigDecimal estimatedAmount) {
		this.estimatedAmount = estimatedAmount;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Integer bookingId;

    @Column(name = "booking_date")
    private LocalDateTime bookingDate;

    @Column(name = "customer_id", nullable = false)
    private Integer customerId;

    @Column(name = "car_id", nullable = false)
    private Integer carId;

    @Column(name = "pickup_hub_id", nullable = false)
    private Integer pickupHubId;

    @Column(name = "drop_hub_id", nullable = false)
    private Integer dropHubId;

    @Column(name = "pickup_datetime", nullable = false)
    private LocalDateTime pickupDatetime;

    @Column(name = "return_datetime", nullable = false)
    private LocalDateTime returnDatetime;

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status")
    private BookingStatus bookingStatus;

    @Column(name = "estimated_amount", precision = 10, scale = 2)
    private BigDecimal estimatedAmount;

    @Column(name = "remarks", length = 255)
    private String remarks;
}