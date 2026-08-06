package com.fleman.dto;

import com.fleman.entity.InvoiceHeader.PaymentType;

public class ProcessReturnRequest {
    private Integer extraMiles = 0;
    private Double extraChargeAmount = 0.0;
    private String damageNotes = "";
    private Integer fuelStatus;

    // How the customer paid at the return desk — CASH, UPI, CARD, or
    // NET_BANKING (per the DB design). Null means "not collected yet",
    // which the service records as payment_status = PENDING.
    private PaymentType paymentType;

    public ProcessReturnRequest() {}

    public Integer getExtraMiles() { return extraMiles; }
    public void setExtraMiles(Integer extraMiles) { this.extraMiles = extraMiles; }
    public Double getExtraChargeAmount() { return extraChargeAmount; }
    public void setExtraChargeAmount(Double extraChargeAmount) { this.extraChargeAmount = extraChargeAmount; }
    public String getDamageNotes() { return damageNotes; }
    public void setDamageNotes(String damageNotes) { this.damageNotes = damageNotes; }
    public Integer getFuelStatus() { return fuelStatus; }
    public void setFuelStatus(Integer fuelStatus) { this.fuelStatus = fuelStatus; }
    public PaymentType getPaymentType() { return paymentType; }
    public void setPaymentType(PaymentType paymentType) { this.paymentType = paymentType; }
}
