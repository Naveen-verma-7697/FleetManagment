/**
 * 
 */
package com.fleetmanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * database
 * INSERT INTO city (city_name, state_id) VALUES
('Mumbai', 1),
('Pune', 1),
('Nagpur', 1),
('Nashik', 1),
('Thane', 1),
('Aurangabad', 1),
('Solapur', 1),
('Kolhapur', 1),
('Amravati', 1),
('Jalgaon', 1);
 */

@Entity
@Table(name = "city")
public class CityMaster {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private int cityId;

@Column(name ="city_name")
private String name;
@Column(name ="state_id")
private int stateId;

public int getCityId() {
	return cityId;
}
public void setCityId(int cityId) {
	this.cityId = cityId;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public int getStateId() {
	return stateId;
}
public void setStateId(int stateId) {
	this.stateId = stateId;
}


}
