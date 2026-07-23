/**
 * 
 */
package com.fleetmanagement.entity;

/**
 * database
 * INSERT INTO car_master
(vehicle_name, fuel_type, seat_capacity, daily_price, weekly_price, monthly_price, status, category_id)
VALUES
('Honda City', 'Petrol', 5, 2500, 15500, 56000, 'AVAILABLE', 1),

('Skoda Slavia', 'Petrol', 5, 3000, 19000, 70000, 'BOOKED', 2),

('Hyundai Creta', 'Diesel', 5, 3500, 22000, 82000, 'NOT_AVAILABLE', 3),

('Maruti Swift', 'Petrol', 5, 1800, 11000, 42000, 'AVAILABLE', 4),

('Tata Punch', 'Petrol', 5, 2200, 14000, 50000, 'AVAILABLE', 5),

('Toyota Fortuner', 'Diesel', 7, 6500, 42000, 150000, 'AVAILABLE', 6),

('BMW 3 Series', 'Petrol', 5, 9000, 60000, 220000, 'AVAILABLE', 7),

('Kia Carens', 'Diesel', 7, 4000, 26000, 95000, 'AVAILABLE', 8),

('Mini Cooper Convertible', 'Petrol', 4, 8500, 56000, 210000, 'AVAILABLE', 9),

('MG ZS EV', 'Electric', 5, 4500, 29000, 105000, 'AVAILABLE', 10);
 * 
 */

import jakarta.persistence.*;

@Entity
@Table(name = "car_master")
public class CarMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_id")
    private int vehicleId;

    @Column(name = "vehicle_name")
    private String vehicleName;

    @Column(name = "fuel_type")
    private String fuelType;

    @Column(name = "seat_capacity")
    private int seatCapacity;

    @Column(name = "daily_price")
    private double dailyPrice;

    @Column(name = "weekly_price")
    private double weeklyPrice;

    @Column(name = "monthly_price")
    private double monthlyPrice;

    @Column(name = "status")
    private String status;

    @Column(name = "category_id")
    private int categoryId;

    // Getters & Setters

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public int getSeatCapacity() {
        return seatCapacity;
    }

    public void setSeatCapacity(int seatCapacity) {
        this.seatCapacity = seatCapacity;
    }

    public double getDailyPrice() {
        return dailyPrice;
    }

    public void setDailyPrice(double dailyPrice) {
        this.dailyPrice = dailyPrice;
    }

    public double getWeeklyPrice() {
        return weeklyPrice;
    }

    public void setWeeklyPrice(double weeklyPrice) {
        this.weeklyPrice = weeklyPrice;
    }

    public double getMonthlyPrice() {
        return monthlyPrice;
    }

    public void setMonthlyPrice(double monthlyPrice) {
        this.monthlyPrice = monthlyPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}
