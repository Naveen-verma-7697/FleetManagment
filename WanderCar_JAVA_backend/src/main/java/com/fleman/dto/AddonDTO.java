package com.fleman.dto;

import java.time.LocalDate;

public class AddonDTO {
    private Long addonId;
    private String addonName;
    private Double dailyRate;
    private LocalDate rateValidFrom;
    private LocalDate rateValidTo;
    private String description;

    public AddonDTO() {}

    public Long getAddonId() { return addonId; }
    public void setAddonId(Long addonId) { this.addonId = addonId; }
    public String getAddonName() { return addonName; }
    public void setAddonName(String addonName) { this.addonName = addonName; }
    public Double getDailyRate() { return dailyRate; }
    public void setDailyRate(Double dailyRate) { this.dailyRate = dailyRate; }
    public LocalDate getRateValidFrom() { return rateValidFrom; }
    public void setRateValidFrom(LocalDate rateValidFrom) { this.rateValidFrom = rateValidFrom; }
    public LocalDate getRateValidTo() { return rateValidTo; }
    public void setRateValidTo(LocalDate rateValidTo) { this.rateValidTo = rateValidTo; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
