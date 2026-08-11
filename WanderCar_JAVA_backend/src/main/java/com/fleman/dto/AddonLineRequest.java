package com.fleman.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AddonLineRequest {
    @NotNull(message = "addonId is required")
    private Long addonId;

    @Min(value = 1, message = "quantity must be at least 1")
    private Integer quantity;

    public AddonLineRequest() {}

    public Long getAddonId() { return addonId; }
    public void setAddonId(Long addonId) { this.addonId = addonId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
