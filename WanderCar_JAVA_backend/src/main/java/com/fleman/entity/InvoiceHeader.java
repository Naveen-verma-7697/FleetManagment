package com.fleman.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// bookingId is "Reference only — No FK" per the DB design (a plain column,
// intentionally not enforced) — every other cross-table column in this
// project now follows that same pattern. customerName/vehicleNumber/hub
// names remain snapshots, not references, same reasoning as before: an
// invoice is a financial record and shouldn't silently change if the
// customer or car is edited later.
@Entity
@Table(name = "invoice_headers")
public class InvoiceHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invoiceId;

    @Column(nullable = false)
    private LocalDateTime invoiceDate;

    @Column(nullable = false)
    private Long bookingId;

    private String customerName;
    private String phone;
    private String email;
    private String vehicleNumber;
    private Long pickupHub;
    private Long dropHub;

    // Actual counter times this invoice was billed against — see
    // StaffServiceImpl.processReturn. Snapshotted onto the invoice (rather
    // than only living on BookingHeader) for the same reason customerName/
    // vehicleNumber are: a financial record shouldn't shift if the booking
    // is edited later.
    private LocalDateTime handoverDatetime;
    private LocalDateTime returnDatetime;

    // Fuel level (25/50/75/100) at hand-over vs. at return, and the
    // resulting shortfall charge — see StaffServiceImpl.processReturn.
    private Integer handoverFuelLevel;
    private Integer returnFuelLevel;
    private Double fuelCharge;

    private Double rentalAmount;
    private Double addonAmount;
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    private String paymentReference;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private Integer extraMiles;
    private Double extraChargeAmount;

    @Column(length = 1000)
    private String damageNotes;

    private Integer days;

    public InvoiceHeader() {}

    public Long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Long invoiceId) { this.invoiceId = invoiceId; }
    public LocalDateTime getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(LocalDateTime invoiceDate) { this.invoiceDate = invoiceDate; }
    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
    public Long getPickupHub() { return pickupHub; }
    public void setPickupHub(Long pickupHub) { this.pickupHub = pickupHub; }
    public Long getDropHub() { return dropHub; }
    public void setDropHub(Long dropHub) { this.dropHub = dropHub; }
    public LocalDateTime getHandoverDatetime() { return handoverDatetime; }
    public void setHandoverDatetime(LocalDateTime handoverDatetime) { this.handoverDatetime = handoverDatetime; }
    public LocalDateTime getReturnDatetime() { return returnDatetime; }
    public void setReturnDatetime(LocalDateTime returnDatetime) { this.returnDatetime = returnDatetime; }
    public Integer getHandoverFuelLevel() { return handoverFuelLevel; }
    public void setHandoverFuelLevel(Integer handoverFuelLevel) { this.handoverFuelLevel = handoverFuelLevel; }
    public Integer getReturnFuelLevel() { return returnFuelLevel; }
    public void setReturnFuelLevel(Integer returnFuelLevel) { this.returnFuelLevel = returnFuelLevel; }
    public Double getFuelCharge() { return fuelCharge; }
    public void setFuelCharge(Double fuelCharge) { this.fuelCharge = fuelCharge; }
    public Double getRentalAmount() { return rentalAmount; }
    public void setRentalAmount(Double rentalAmount) { this.rentalAmount = rentalAmount; }
    public Double getAddonAmount() { return addonAmount; }
    public void setAddonAmount(Double addonAmount) { this.addonAmount = addonAmount; }
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    public PaymentType getPaymentType() { return paymentType; }
    public void setPaymentType(PaymentType paymentType) { this.paymentType = paymentType; }
    public String getPaymentReference() { return paymentReference; }
    public void setPaymentReference(String paymentReference) { this.paymentReference = paymentReference; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public Integer getExtraMiles() { return extraMiles; }
    public void setExtraMiles(Integer extraMiles) { this.extraMiles = extraMiles; }
    public Double getExtraChargeAmount() { return extraChargeAmount; }
    public void setExtraChargeAmount(Double extraChargeAmount) { this.extraChargeAmount = extraChargeAmount; }
    public String getDamageNotes() { return damageNotes; }
    public void setDamageNotes(String damageNotes) { this.damageNotes = damageNotes; }
    public Integer getDays() { return days; }
    public void setDays(Integer days) { this.days = days; }

    // UPI included per the DB design — and a natural fit for an India-based deployment.
    public enum PaymentType { CASH, UPI, CARD, NET_BANKING }
    public enum PaymentStatus { PENDING, PAID, FAILED }
}
