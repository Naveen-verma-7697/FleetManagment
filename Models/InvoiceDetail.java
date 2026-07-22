package com.fleetmanagement.Models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "invoice_detail")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InvoiceDetail {

    public Integer getInvoiceDetailId() {
		return invoiceDetailId;
	}

	public void setInvoiceDetailId(Integer invoiceDetailId) {
		this.invoiceDetailId = invoiceDetailId;
	}

	public Integer getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(Integer invoiceId) {
		this.invoiceId = invoiceId;
	}

	public String getAddonName() {
		return addonName;
	}

	public void setAddonName(String addonName) {
		this.addonName = addonName;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getAddonRate() {
		return addonRate;
	}

	public void setAddonRate(BigDecimal addonRate) {
		this.addonRate = addonRate;
	}

	public BigDecimal getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_detail_id")
    private Integer invoiceDetailId;

    @Column(name = "invoice_id", nullable = false)
    private Integer invoiceId;

    @Column(name = "addon_name", length = 100)
    private String addonName;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "addon_rate", precision = 10, scale = 2)
    private BigDecimal addonRate;

    @Column(name = "subtotal", precision = 10, scale = 2)
    private BigDecimal subtotal;
}
