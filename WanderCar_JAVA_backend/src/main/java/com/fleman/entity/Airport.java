package com.fleman.entity;

import jakarta.persistence.*;

// city_id, state_id, hub_id are all plain logical-reference columns now —
// including hub_id, which the original DB design marked as a literal "fk"
// but this project no longer uses database-level foreign keys anywhere.
@Entity
@Table(name = "airports")
public class Airport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long airportId;

    @Column(nullable = false, unique = true, length = 10)
    private String airportCode;

    @Column(nullable = false, length = 150)
    private String airportName;

    @Column(nullable = false)
    private Long cityId;

    @Column(nullable = false)
    private Long stateId;

    @Column(nullable = false)
    private Long hubId;

    public Airport() {}

    public Long getAirportId() { return airportId; }
    public void setAirportId(Long airportId) { this.airportId = airportId; }
    public String getAirportCode() { return airportCode; }
    public void setAirportCode(String airportCode) { this.airportCode = airportCode; }
    public String getAirportName() { return airportName; }
    public void setAirportName(String airportName) { this.airportName = airportName; }
    public Long getCityId() { return cityId; }
    public void setCityId(Long cityId) { this.cityId = cityId; }
    public Long getStateId() { return stateId; }
    public void setStateId(Long stateId) { this.stateId = stateId; }
    public Long getHubId() { return hubId; }
    public void setHubId(Long hubId) { this.hubId = hubId; }
}
