package com.fleetmanagement.Models;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "car_type")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CarType {

    public Integer getCarTypeId() {
		return carTypeId;
	}

	public void setCarTypeId(Integer carTypeId) {
		this.carTypeId = carTypeId;
	}

	public String getCarTypeName() {
		return carTypeName;
	}

	public void setCarTypeName(String carTypeName) {
		this.carTypeName = carTypeName;
	}

	public BigDecimal getDailyRate() {
		return dailyRate;
	}

	public void setDailyRate(BigDecimal dailyRate) {
		this.dailyRate = dailyRate;
	}

	public BigDecimal getWeeklyRate() {
		return weeklyRate;
	}

	public void setWeeklyRate(BigDecimal weeklyRate) {
		this.weeklyRate = weeklyRate;
	}

	public BigDecimal getMonthlyRate() {
		return monthlyRate;
	}

	public void setMonthlyRate(BigDecimal monthlyRate) {
		this.monthlyRate = monthlyRate;
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

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_type_id")
    private Integer carTypeId;

    @Column(name = "car_type_name", nullable = false, unique = true, length = 100)
    private String carTypeName;

    @Column(name = "daily_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal dailyRate;

    @Column(name = "weekly_rate", precision = 10, scale = 2)
    private BigDecimal weeklyRate;

    @Column(name = "monthly_rate", precision = 10, scale = 2)
    private BigDecimal monthlyRate;

    @Column(name = "rate_valid_from", nullable = false)
    private LocalDate rateValidFrom;

    @Column(name = "rate_valid_to", nullable = false)
    private LocalDate rateValidTo;

    @Column(name = "image_path", length = 255)
    private String imagePath;
}
