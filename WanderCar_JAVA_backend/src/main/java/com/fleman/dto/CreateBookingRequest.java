package com.fleman.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

// Matches VehicleSelectionPage's new category picker: the customer now
// reserves a carTypeId, not a specific carId — the actual vehicle is
// assigned by staff at hand-over (see HandoverRequest). The frontend does
// not send prices for add-ons either, only addonId + quantity; the server
// looks up the current rate and computes every subtotal itself.
public class CreateBookingRequest {
    @NotNull(message = "customerId is required")
    private Long customerId;

    @NotNull(message = "carTypeId is required")
    private Long carTypeId;

    @NotNull(message = "pickupHubId is required")
    private Long pickupHubId;

    private Long dropHubId;

    @NotNull(message = "pickupDatetime is required")
    private LocalDateTime pickupDatetime;

    @NotNull(message = "returnDatetime is required")
    private LocalDateTime returnDatetime;

    private List<AddonLineRequest> addons;

    // Sent by the client for display purposes only — the server recomputes
    // and trusts its own total, never this value, when persisting.
    private Double estimatedAmount;

    private String remarks;

    public CreateBookingRequest() {}

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public Long getCarTypeId() { return carTypeId; }
    public void setCarTypeId(Long carTypeId) { this.carTypeId = carTypeId; }
    public Long getPickupHubId() { return pickupHubId; }
    public void setPickupHubId(Long pickupHubId) { this.pickupHubId = pickupHubId; }
    public Long getDropHubId() { return dropHubId; }
    public void setDropHubId(Long dropHubId) { this.dropHubId = dropHubId; }
    public LocalDateTime getPickupDatetime() { return pickupDatetime; }
    public void setPickupDatetime(LocalDateTime pickupDatetime) { this.pickupDatetime = pickupDatetime; }
    public LocalDateTime getReturnDatetime() { return returnDatetime; }
    public void setReturnDatetime(LocalDateTime returnDatetime) { this.returnDatetime = returnDatetime; }
    public List<AddonLineRequest> getAddons() { return addons; }
    public void setAddons(List<AddonLineRequest> addons) { this.addons = addons; }
    public Double getEstimatedAmount() { return estimatedAmount; }
    public void setEstimatedAmount(Double estimatedAmount) { this.estimatedAmount = estimatedAmount; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
