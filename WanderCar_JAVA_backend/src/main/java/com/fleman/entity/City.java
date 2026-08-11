package com.fleman.entity;

import jakarta.persistence.*;

// No @ManyToOne here — stateId is a plain column, a "logical reference"
// only (matching the DB design sheet's own REMARKS: "Logical Ref: State
// Master"). No database-level foreign key constraint is created; the
// service layer is responsible for validating that a stateId actually
// exists before it's written.
@Entity
@Table(name = "cities")
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cityId;

    @Column(nullable = false, length = 100)
    private String cityName;

    @Column(nullable = false)
    private Long stateId;

    public City() {}

    public City(String cityName, Long stateId) {
        this.cityName = cityName;
        this.stateId = stateId;
    }

    public Long getCityId() { return cityId; }
    public void setCityId(Long cityId) { this.cityId = cityId; }
    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }
    public Long getStateId() { return stateId; }
    public void setStateId(Long stateId) { this.stateId = stateId; }
}
