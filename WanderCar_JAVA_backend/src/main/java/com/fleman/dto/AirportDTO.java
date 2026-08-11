package com.fleman.dto;

public class AirportDTO {
    private Long airportId;
    private String airportCode;
    private String airportName;
    private Long cityId;
    private Long stateId;
    private Long hubId;

    public AirportDTO() {}

    public AirportDTO(Long airportId, String airportCode, String airportName,
                       Long cityId, Long stateId, Long hubId) {
        this.airportId = airportId;
        this.airportCode = airportCode;
        this.airportName = airportName;
        this.cityId = cityId;
        this.stateId = stateId;
        this.hubId = hubId;
    }

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
