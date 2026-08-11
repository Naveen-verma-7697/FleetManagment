package com.fleman.dto;

// One row of the staff dashboard: how many cars of this type exist (at the
// hub in question, or fleet-wide if no hub was specified), and how they're
// split across available / handed-over / in-maintenance right now.
public class CarTypeAvailabilityDTO {
    private Long carTypeId;
    private String carTypeName;
    private long totalCars;
    private long availableCars;
    private long bookedCars;
    private long maintenanceCars;

    public CarTypeAvailabilityDTO() {}

    public CarTypeAvailabilityDTO(Long carTypeId, String carTypeName, long totalCars,
                                   long availableCars, long bookedCars, long maintenanceCars) {
        this.carTypeId = carTypeId;
        this.carTypeName = carTypeName;
        this.totalCars = totalCars;
        this.availableCars = availableCars;
        this.bookedCars = bookedCars;
        this.maintenanceCars = maintenanceCars;
    }

    public Long getCarTypeId() { return carTypeId; }
    public void setCarTypeId(Long carTypeId) { this.carTypeId = carTypeId; }
    public String getCarTypeName() { return carTypeName; }
    public void setCarTypeName(String carTypeName) { this.carTypeName = carTypeName; }
    public long getTotalCars() { return totalCars; }
    public void setTotalCars(long totalCars) { this.totalCars = totalCars; }
    public long getAvailableCars() { return availableCars; }
    public void setAvailableCars(long availableCars) { this.availableCars = availableCars; }
    public long getBookedCars() { return bookedCars; }
    public void setBookedCars(long bookedCars) { this.bookedCars = bookedCars; }
    public long getMaintenanceCars() { return maintenanceCars; }
    public void setMaintenanceCars(long maintenanceCars) { this.maintenanceCars = maintenanceCars; }
}
