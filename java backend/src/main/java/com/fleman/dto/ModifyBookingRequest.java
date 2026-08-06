package com.fleman.dto;

import java.time.LocalDateTime;
import java.util.List;

// A partial patch, same idea as UpdateCustomerRequest — any null field is
// left unchanged. Mirrors the frontend's modifyBooking(confirmationNo, patch).
// carTypeId was added so "modify booking" can change the reserved category,
// not just hub/dates — the frontend redirects the user to the vehicle or
// add-ons page first to pick the new values, then submits the whole thing
// back through this same endpoint. Only allowed while the booking hasn't
// been handed over yet (BookingServiceImpl.modifyBooking enforces this,
// since once a real car is with the customer its category can't just change).
public class ModifyBookingRequest {
    private Long pickupHubId;
    private Long dropHubId;
    private LocalDateTime pickupDatetime;
    private LocalDateTime returnDatetime;
    private Long carTypeId;
    // Null means "leave the add-ons as they are"; an empty list means "the
    // customer removed every add-on" — modifyBooking distinguishes the two.
    private List<AddonLineRequest> addons;
    private String remarks;

    public ModifyBookingRequest() {}

    public Long getPickupHubId() { return pickupHubId; }
    public void setPickupHubId(Long pickupHubId) { this.pickupHubId = pickupHubId; }
    public Long getDropHubId() { return dropHubId; }
    public void setDropHubId(Long dropHubId) { this.dropHubId = dropHubId; }
    public LocalDateTime getPickupDatetime() { return pickupDatetime; }
    public void setPickupDatetime(LocalDateTime pickupDatetime) { this.pickupDatetime = pickupDatetime; }
    public LocalDateTime getReturnDatetime() { return returnDatetime; }
    public void setReturnDatetime(LocalDateTime returnDatetime) { this.returnDatetime = returnDatetime; }
    public Long getCarTypeId() { return carTypeId; }
    public void setCarTypeId(Long carTypeId) { this.carTypeId = carTypeId; }
    public List<AddonLineRequest> getAddons() { return addons; }
    public void setAddons(List<AddonLineRequest> addons) { this.addons = addons; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
