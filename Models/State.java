package com.fleetmanagement.Models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "state")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class State {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "state_id")
    private Integer stateId;

    @Column(name = "state_name", nullable = false, unique = true, length = 100)
    private String stateName;

	public Integer getStateId() {
		return stateId;
	}

	public void setStateId(Integer stateId) {
		this.stateId = stateId;
	}

	

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}
}