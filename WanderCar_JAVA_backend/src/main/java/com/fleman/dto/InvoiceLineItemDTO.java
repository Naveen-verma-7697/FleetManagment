package com.fleman.dto;

public class InvoiceLineItemDTO {
    private String addonName;
    private Integer quantity;
    private Double addonRate;
    private Double subtotal;

    public InvoiceLineItemDTO() {}

    public String getAddonName() { return addonName; }
    public void setAddonName(String addonName) { this.addonName = addonName; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Double getAddonRate() { return addonRate; }
    public void setAddonRate(Double addonRate) { this.addonRate = addonRate; }
    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }
}
