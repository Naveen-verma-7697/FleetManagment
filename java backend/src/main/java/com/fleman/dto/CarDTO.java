package com.fleman.dto;

// Mirrors what the frontend's getAvailableCars()/getCarById() already
// return: the car's own fields plus a nested carType object, because
// VehicleSelectionPage reads car.carType.dailyRate directly.
public class CarDTO {
    private Long carId;
    private Long carTypeId;
    private Long hubId;
    private String vehicleNumber;
    private String brand;
    private String model;
    private Integer manufactureYear;
    private String color;
    private String fuelType;
    private Integer seatingCapacity;
    private Double mileage;
    private Integer odometer;
    private Integer fuelLevel;
    private boolean isAvailable;
    private String status;
    // Distinct from `status`/`isAvailable` (the car's persisted, not-date-
    // specific state): true only when this car has no CONFIRMED/ONGOING
    // booking overlapping the requested pickup/return window. A car can be
    // status=AVAILABLE fleet-wide and still have bookableNow=false for a
    // specific date range that's already spoken for.
    private boolean bookableNow;
    private CarTypeDTO carType;

    public CarDTO() {}

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
    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }
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
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isBookableNow() { return bookableNow; }
    public void setBookableNow(boolean bookableNow) { this.bookableNow = bookableNow; }
    public CarTypeDTO getCarType() { return carType; }
    public void setCarType(CarTypeDTO carType) { this.carType = carType; }
}
