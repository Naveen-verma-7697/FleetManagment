package com.fleetmanagement.Models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "city")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class City {

    public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public Integer getStateId() {
		return stateId;
	}

	public void setStateId(Integer stateId) {
		this.stateId = stateId;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "city_id")
    private Integer cityId;

    @Column(name = "city_name", nullable = false, length = 100)
    private String cityName;

    @Column(name = "state_id", nullable = false)
    private Integer stateId;
}
