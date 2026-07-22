package com.fleetmanagement.Models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "addon")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Addon {

    public Integer getAddonId() {
		return addonId;
	}

	public void setAddonId(Integer addonId) {
		this.addonId = addonId;
	}

	public String getAddonName() {
		return addonName;
	}

	public void setAddonName(String addonName) {
		this.addonName = addonName;
	}

	public BigDecimal getDailyRate() {
		return dailyRate;
	}

	public void setDailyRate(BigDecimal dailyRate) {
		this.dailyRate = dailyRate;
	}

	public LocalDate getRateValidFrom() {
		return rateValidFrom;
	}

	public void setRateValidFrom(LocalDate rateValidFrom) {
		this.rateValidFrom = rateValidFrom;
	}

	public LocalDate getRateValidTo() {
		return rateValidTo;
	}

	public void setRateValidTo(LocalDate rateValidTo) {
		this.rateValidTo = rateValidTo;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "addon_id")
    private Integer addonId;

    @Column(name = "addon_name", nullable = false, unique = true, length = 100)
    private String addonName;

    @Column(name = "daily_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal dailyRate;

    @Column(name = "rate_valid_from", nullable = false)
    private LocalDate rateValidFrom;

    @Column(name = "rate_valid_to", nullable = false)
    private LocalDate rateValidTo;
}