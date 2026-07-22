package com.fleetmanagement.Models;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "car")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Car {

    public Integer getCarId() {
		return carId;
	}

	public void setCarId(Integer carId) {
		this.carId = carId;
	}

	public Integer getCarTypeId() {
		return carTypeId;
	}

	public void setCarTypeId(Integer carTypeId) {
		this.carTypeId = carTypeId;
	}

	public Integer getHubId() {
		return hubId;
	}

	public void setHubId(Integer hubId) {
		this.hubId = hubId;
	}

	public String getVehicleNumber() {
		return vehicleNumber;
	}

	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public Integer getManufactureYear() {
		return manufactureYear;
	}

	public void setManufactureYear(Integer manufactureYear) {
		this.manufactureYear = manufactureYear;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public FuelType getFuelType() {
		return fuelType;
	}

	public void setFuelType(FuelType fuelType) {
		this.fuelType = fuelType;
	}

	public Integer getSeatingCapacity() {
		return seatingCapacity;
	}

	public void setSeatingCapacity(Integer seatingCapacity) {
		this.seatingCapacity = seatingCapacity;
	}

	public BigDecimal getMileage() {
		return mileage;
	}

	public void setMileage(BigDecimal mileage) {
		this.mileage = mileage;
	}

	public Integer getOdometer() {
		return odometer;
	}

	public void setOdometer(Integer odometer) {
		this.odometer = odometer;
	}

	public BigDecimal getFuelLevel() {
		return fuelLevel;
	}

	public void setFuelLevel(BigDecimal fuelLevel) {
		this.fuelLevel = fuelLevel;
	}

	public Boolean getIsAvailable() {
		return isAvailable;
	}

	public void setIsAvailable(Boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	public CarStatus getStatus() {
		return status;
	}

	public void setStatus(CarStatus status) {
		this.status = status;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_id")
    private Integer carId;

    @Column(name = "car_type_id", nullable = false)
    private Integer carTypeId;

    @Column(name = "hub_id", nullable = false)
    private Integer hubId;

    @Column(name = "vehicle_number", nullable = false, unique = true, length = 20)
    private String vehicleNumber;

    @Column(name = "brand", nullable = false, length = 50)
    private String brand;

    @Column(name = "model", nullable = false, length = 50)
    private String model;

    @Column(name = "manufacture_year")
    private Integer manufactureYear;

    @Column(name = "color", length = 30)
    private String color;

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type")
    private FuelType fuelType;

    @Column(name = "seating_capacity")
    private Integer seatingCapacity;

    @Column(name = "mileage", precision = 6, scale = 2)
    private BigDecimal mileage;

    @Column(name = "odometer")
    private Integer odometer;

    @Column(name = "fuel_level", precision = 5, scale = 2)
    private BigDecimal fuelLevel;

    @Column(name = "is_available")
    private Boolean isAvailable;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CarStatus status;
}