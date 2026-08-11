package com.fleman.entity;

// bookingId, addonId are plain logical-reference columns — no @ManyToOne.
import jakarta.persistence.*;

@Entity
@Table(name = "booking_details")
public class BookingDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingDetailId;

    @Column(nullable = false)
    private Long bookingId;

    @Column(nullable = false)
    private Long addonId;

    private Double addonRate;
    private Integer quantity;
    private Double subtotal;

    public BookingDetail() {}

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
}
