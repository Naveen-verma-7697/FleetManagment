package com.fleman.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "addons")
public class Addon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addonId;

    @Column(nullable = false, unique = true, length = 100)
    private String addonName;

    @Column(nullable = false)
    private Double dailyRate;
    private LocalDate rateValidFrom;
    private LocalDate rateValidTo;

    @Column(length = 500)
    private String description;

    public Addon() {}

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
