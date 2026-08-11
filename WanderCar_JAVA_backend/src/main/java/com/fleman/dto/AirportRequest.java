package com.fleman.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Create/update payload for the Airport Master API — field lengths and
// required-ness match the "Airport Master" sheet of the DB design exactly:
// airport_name VARCHAR(150) NOT NULL, airport_code VARCHAR(10) UNIQUE NOT
// NULL, city_id/state_id/hub_id all required (Logical Ref / fk, NULL? = No).
public class AirportRequest {

    @NotBlank(message = "airportName is required")
    @Size(max = 150, message = "airportName must be at most 150 characters")
    private String airportName;

    @NotBlank(message = "airportCode is required")
    @Size(max = 10, message = "airportCode must be at most 10 characters")
    private String airportCode;

    @NotNull(message = "cityId is required")
    private Long cityId;

    @NotNull(message = "stateId is required")
    private Long stateId;

    @NotNull(message = "hubId is required")
    private Long hubId;

    public AirportRequest() {}

    public String getAirportName() { return airportName; }
    public void setAirportName(String airportName) { this.airportName = airportName; }
    public String getAirportCode() { return airportCode; }
    public void setAirportCode(String airportCode) { this.airportCode = airportCode; }
    public Long getCityId() { return cityId; }
    public void setCityId(Long cityId) { this.cityId = cityId; }
    public Long getStateId() { return stateId; }
    public void setStateId(Long stateId) { this.stateId = stateId; }
    public Long getHubId() { return hubId; }
    public void setHubId(Long hubId) { this.hubId = hubId; }
}
