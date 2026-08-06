package com.fleman.entity;

import jakarta.persistence.*;

// carTypeId and hubId are plain logical-reference columns — no @ManyToOne.
@Entity
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long carId;

    @Column(nullable = false)
    private Long carTypeId;

    @Column(nullable = false)
    private Long hubId;

    @Column(nullable = false, unique = true, length = 20)
    private String vehicleNumber;

    @Column(length = 50)
    private String brand;

    @Column(length = 50)
    private String model;

    private Integer manufactureYear;

    @Column(length = 30)
    private String color;

    @Enumerated(EnumType.STRING)
    private FuelType fuelType;

    private Integer seatingCapacity;
    private Double mileage;
    private Integer odometer;
    private Integer fuelLevel;
    private boolean isAvailable;

    @Enumerated(EnumType.STRING)
    private CarStatus status;

    public Car() {}

    public Long getCarId() { return carId; }
    public void setCarId(Long carId) { this.carId = carId; }
    public Long getCarTypeId() { return carTypeId; }
    public void setCarTypeId(Long carTypeId) { this.carTypeId = carTypeId; }
    public Long getHubId() { return hubId; }
    public void setHubId(Long hubId) { this.hubId = hubId; }
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public Integer getManufactureYear() { return manufactureYear; }
    public void setManufactureYear(Integer manufactureYear) { this.manufactureYear = manufactureYear; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public FuelType getFuelType() { return fuelType; }
    public void setFuelType(FuelType fuelType) { this.fuelType = fuelType; }
    public Integer getSeatingCapacity() { return seatingCapacity; }
    public void setSeatingCapacity(Integer seatingCapacity) { this.seatingCapacity = seatingCapacity; }
    public Double getMileage() { return mileage; }
    public void setMileage(Double mileage) { this.mileage = mileage; }
    public Integer getOdometer() { return odometer; }
    public void setOdometer(Integer odometer) { this.odometer = odometer; }
    public Integer getFuelLevel() { return fuelLevel; }
    public void setFuelLevel(Integer fuelLevel) { this.fuelLevel = fuelLevel; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
    public CarStatus getStatus() { return status; }
    public void setStatus(CarStatus status) { this.status = status; }

    public enum FuelType { PETROL, DIESEL, CNG, ELECTRIC, HYBRID }
    public enum CarStatus { AVAILABLE, BOOKED, UNDER_MAINTENANCE }
}
