package com.fleman.dto;

public class CityDTO {
    private Long cityId;
    private String cityName;
    private Long stateId;

    public CityDTO() {}
    public CityDTO(Long cityId, String cityName, Long stateId) {
        this.cityId = cityId;
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
