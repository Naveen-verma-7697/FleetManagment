package com.fleetmanagement.Models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "airport")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Airport {

    public Integer getAirportId() {
		return airportId;
	}

	public void setAirportId(Integer airportId) {
		this.airportId = airportId;
	}

	public String getAirportName() {
		return airportName;
	}

	public void setAirportName(String airportName) {
		this.airportName = airportName;
	}

	public String getAirportCode() {
		return airportCode;
	}

	public void setAirportCode(String airportCode) {
		this.airportCode = airportCode;
	}

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public Integer getStateId() {
		return stateId;
	}

	public void setStateId(Integer stateId) {
		this.stateId = stateId;
	}

	public Integer getHubId() {
		return hubId;
	}

	public void setHubId(Integer hubId) {
		this.hubId = hubId;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "airport_id")
    private Integer airportId;

    @Column(name = "airport_name", nullable = false, length = 150)
    private String airportName;

    @Column(name = "airport_code", nullable = false, unique = true, length = 10)
    private String airportCode;

    @Column(name = "city_id", nullable = false)
    private Integer cityId;

    @Column(name = "state_id", nullable = false)
    private Integer stateId;

    @Column(name = "hub_id", nullable = false)
    private Integer hubId;
}
