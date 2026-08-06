package com.fleman.dto;

public class BookingDetailDTO {
    private Long bookingDetailId;
    private Long bookingId;
    private Long addonId;
    private Double addonRate;
    private Integer quantity;
    private Double subtotal;
    private AddonDTO addon;

    public BookingDetailDTO() {}

    public Long getBookingDetailId() { return bookingDetailId; }
    public void setBookingDetailId(Long bookingDetailId) { this.bookingDetailId = bookingDetailId; }
    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
    public Long getAddonId() { return addonId; }
    public void setAddonId(Long addonId) { this.addonId = addonId; }
    public Double getAddonRate() { return addonRate; }
    public void setAddonRate(Double addonRate) { this.addonRate = addonRate; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }
    public AddonDTO getAddon() { return addon; }
    public void setAddon(AddonDTO addon) { this.addon = addon; }
}
