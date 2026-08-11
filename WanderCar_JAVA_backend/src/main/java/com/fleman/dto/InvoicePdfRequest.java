package com.fleman.dto;

import java.time.LocalDateTime;
import java.util.List;

// Self-contained payload for cross-service PDF generation (see
// InvoiceController.generateInvoicePdf) — the caller (the .NET backend, or
// this app itself) already owns the invoice/booking/customer data, so this
// service never looks anything up by id. That sidesteps the two backends
// having separate databases with independently auto-incrementing ids, where
// an invoiceId on one side means nothing on the other.
public class InvoicePdfRequest {
    private String customerName;
    private String email;
    private String phone;
    private String confirmationNo;
    private String vehicleNumber;
    private LocalDateTime handoverDatetime;
    private LocalDateTime returnDatetime;
    private Integer days;
    private Double rentalAmount;
    private Double addonAmount;
    private Double fuelCharge;
    private Integer handoverFuelLevel;
    private Integer returnFuelLevel;
    private Double extraChargeAmount;
    private Integer extraMiles;
    private String damageNotes;
    private Double totalAmount;
    private String paymentType;
    private String paymentStatus;
    private String paymentReference;
    private List<InvoiceLineItemDTO> lines;

    public InvoicePdfRequest() {}

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getConfirmationNo() { return confirmationNo; }
    public void setConfirmationNo(String confirmationNo) { this.confirmationNo = confirmationNo; }
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
    public LocalDateTime getHandoverDatetime() { return handoverDatetime; }
    public void setHandoverDatetime(LocalDateTime handoverDatetime) { this.handoverDatetime = handoverDatetime; }
    public LocalDateTime getReturnDatetime() { return returnDatetime; }
    public void setReturnDatetime(LocalDateTime returnDatetime) { this.returnDatetime = returnDatetime; }
    public Integer getDays() { return days; }
    public void setDays(Integer days) { this.days = days; }
    public Double getRentalAmount() { return rentalAmount; }
    public void setRentalAmount(Double rentalAmount) { this.rentalAmount = rentalAmount; }
    public Double getAddonAmount() { return addonAmount; }
    public void setAddonAmount(Double addonAmount) { this.addonAmount = addonAmount; }
    public Double getFuelCharge() { return fuelCharge; }
    public void setFuelCharge(Double fuelCharge) { this.fuelCharge = fuelCharge; }
    public Integer getHandoverFuelLevel() { return handoverFuelLevel; }
    public void setHandoverFuelLevel(Integer handoverFuelLevel) { this.handoverFuelLevel = handoverFuelLevel; }
    public Integer getReturnFuelLevel() { return returnFuelLevel; }
    public void setReturnFuelLevel(Integer returnFuelLevel) { this.returnFuelLevel = returnFuelLevel; }
    public Double getExtraChargeAmount() { return extraChargeAmount; }
    public void setExtraChargeAmount(Double extraChargeAmount) { this.extraChargeAmount = extraChargeAmount; }
    public Integer getExtraMiles() { return extraMiles; }
    public void setExtraMiles(Integer extraMiles) { this.extraMiles = extraMiles; }
    public String getDamageNotes() { return damageNotes; }
    public void setDamageNotes(String damageNotes) { this.damageNotes = damageNotes; }
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getPaymentReference() { return paymentReference; }
    public void setPaymentReference(String paymentReference) { this.paymentReference = paymentReference; }
    public List<InvoiceLineItemDTO> getLines() { return lines; }
    public void setLines(List<InvoiceLineItemDTO> lines) { this.lines = lines; }
}
