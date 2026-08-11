package com.fleman.entity;

import jakarta.persistence.*;

// invoiceId is a plain logical-reference column — no @ManyToOne.
@Entity
@Table(name = "invoice_details")
public class InvoiceDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invoiceDetailId;

    @Column(nullable = false)
    private Long invoiceId;

    private String addonName;
    private Integer quantity;
    private Double addonRate;
    private Double subtotal;

    public InvoiceDetail() {}

    public Long getInvoiceDetailId() { return invoiceDetailId; }
    public void setInvoiceDetailId(Long invoiceDetailId) { this.invoiceDetailId = invoiceDetailId; }
    public Long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Long invoiceId) { this.invoiceId = invoiceId; }
    public String getAddonName() { return addonName; }
    public void setAddonName(String addonName) { this.addonName = addonName; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Double getAddonRate() { return addonRate; }
    public void setAddonRate(Double addonRate) { this.addonRate = addonRate; }
    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }
}
