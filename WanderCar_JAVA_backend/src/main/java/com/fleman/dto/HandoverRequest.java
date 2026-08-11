package com.fleman.dto;

// carId is now required: the booking only reserved a category, so staff
// must pick which specific car of that type (at the pickup hub) actually
// goes to the customer. StaffServiceImpl.handoverVehicle validates it's
// the right type, the right hub, and still AVAILABLE before assigning it.
public class HandoverRequest {
    private Long carId;
    private Integer fuelStatus;
    private String notes;

    public HandoverRequest() {}

    public Long getCarId() { return carId; }
    public void setCarId(Long carId) { this.carId = carId; }
    public Integer getFuelStatus() { return fuelStatus; }
    public void setFuelStatus(Integer fuelStatus) { this.fuelStatus = fuelStatus; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
