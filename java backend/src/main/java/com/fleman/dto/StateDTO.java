package com.fleman.dto;

public class StateDTO {
    private Long stateId;
    private String stateName;

    public StateDTO() {}
    public StateDTO(Long stateId, String stateName) {
        this.stateId = stateId;
        this.stateName = stateName;
    }

    public Long getStateId() { return stateId; }
    public void setStateId(Long stateId) { this.stateId = stateId; }
    public String getStateName() { return stateName; }
    public void setStateName(String stateName) { this.stateName = stateName; }
}
