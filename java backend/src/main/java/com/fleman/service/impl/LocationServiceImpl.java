package com.fleman.service.impl;

import com.fleman.dto.CityDTO;
import com.fleman.dto.HubDTO;
import com.fleman.dto.StateDTO;
import com.fleman.entity.Airport;
import com.fleman.entity.City;
import com.fleman.entity.Hub;
import com.fleman.entity.State;
import com.fleman.exception.ResourceNotFoundException;
import com.fleman.repository.AirportRepository;
import com.fleman.repository.CityRepository;
import com.fleman.repository.HubRepository;
import com.fleman.repository.StateRepository;
import com.fleman.service.LocationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationServiceImpl implements LocationService {

    private final StateRepository stateRepository;
    private final CityRepository cityRepository;
    private final HubRepository hubRepository;
    private final AirportRepository airportRepository;

    public LocationServiceImpl(StateRepository stateRepository, CityRepository cityRepository,
                                HubRepository hubRepository, AirportRepository airportRepository) {
        this.stateRepository = stateRepository;
        this.cityRepository = cityRepository;
        this.hubRepository = hubRepository;
        this.airportRepository = airportRepository;
    }

    @Override
    public List<StateDTO> getStates() {
        return stateRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<CityDTO> getCitiesByState(Long stateId) {
        List<City> results = stateId == null ? cityRepository.findAll() : cityRepository.findByStateId(stateId);
        return results.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<HubDTO> getAllHubs() {
    	List<Hub> hubs = hubRepository.findAll();

    	System.out.println("Total hubs = " + hubs.size());

    	hubs.forEach(h ->
    	    System.out.println(
    	        h.getHubId() + " "
    	        + h.getHubName() + " "
    	        + h.getCityId()
    	    )
    	);

    	return hubs.stream()
    	        .map(this::toDto)
    	        .collect(Collectors.toList());
    }

    @Override
    public HubDTO getHubById(Long hubId) {
        return hubRepository.findById(hubId).map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Hub not found: " + hubId));
    }

    @Override
    public CityDTO getCityById(Long cityId) {
        return cityRepository.findById(cityId).map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("City not found: " + cityId));
    }

    @Override
    public StateDTO getStateById(Long stateId) {
        return stateRepository.findById(stateId).map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("State not found: " + stateId));
    }

    @Override
    public HubDTO findHubForCity(Long cityId) {
        return hubRepository.findByCityId(cityId).map(this::toDto).orElse(null);
    }

    @Override
    public HubDTO findHubForAirport(Long airportId) {
        Airport airport = airportRepository.findById(airportId).orElse(null);
        if (airport == null) return null;
        // No relationship traversal — a second, explicit lookup by the
        // plain hubId column, same as any other logical reference.
        return hubRepository.findById(airport.getHubId()).map(this::toDto).orElse(null);
    }

    private StateDTO toDto(State s) {
        return new StateDTO(s.getStateId(), s.getStateName());
    }

    private CityDTO toDto(City c) {
        return new CityDTO(c.getCityId(), c.getCityName(), c.getStateId());
    }

    private HubDTO toDto(Hub h) {
        return new HubDTO(h.getHubId(), h.getHubName(), h.getAddress(),
                h.getCityId(), h.getStateId(),
                h.getPincode(), h.getContactNo(), h.getEmail());
    }
}
