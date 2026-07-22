package com.fleetmanagement.Models;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "booking_detail")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookingDetail {

    public Integer getBookingDetailId() {
		return bookingDetailId;
	}

	public void setBookingDetailId(Integer bookingDetailId) {
		this.bookingDetailId = bookingDetailId;
	}

	public Integer getBookingId() {
		return bookingId;
	}

	public void setBookingId(Integer bookingId) {
		this.bookingId = bookingId;
	}

	public Integer getAddonId() {
		return addonId;
	}

	public void setAddonId(Integer addonId) {
		this.addonId = addonId;
	}

	public BigDecimal getAddonRate() {
		return addonRate;
	}

	public void setAddonRate(BigDecimal addonRate) {
		this.addonRate = addonRate;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_detail_id")
    private Integer bookingDetailId;

    @Column(name = "booking_id", nullable = false)
    private Integer bookingId;

    @Column(name = "addon_id", nullable = false)
    private Integer addonId;

    @Column(name = "addon_rate", precision = 10, scale = 2)
    private BigDecimal addonRate;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "subtotal", precision = 10, scale = 2)
    private BigDecimal subtotal;
}