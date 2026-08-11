package com.fleman.service;

import com.fleman.dto.CityDTO;
import com.fleman.dto.HubDTO;
import com.fleman.dto.StateDTO;

import java.util.List;

public interface LocationService {
    List<StateDTO> getStates();
    List<CityDTO> getCitiesByState(Long stateId);
    List<HubDTO> getAllHubs();
    HubDTO getHubById(Long hubId);
    CityDTO getCityById(Long cityId);
    StateDTO getStateById(Long stateId);
    HubDTO findHubForCity(Long cityId);

    // Resolves an airport's kiosk hub — kept here (not in AirportService)
    // because the return type and purpose are about Hub resolution, not
    // Airport Master data itself.
    HubDTO findHubForAirport(Long airportId);
}
