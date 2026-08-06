package com.fleman.service.impl;

import com.fleman.dto.AirportDTO;
import com.fleman.dto.AirportRequest;
import com.fleman.entity.Airport;
import com.fleman.exception.ApiException;
import com.fleman.exception.ResourceNotFoundException;
import com.fleman.repository.AirportRepository;
import com.fleman.repository.CityRepository;
import com.fleman.repository.HubRepository;
import com.fleman.repository.StateRepository;
import com.fleman.service.AirportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AirportServiceImpl implements AirportService {

    private final AirportRepository airportRepository;
    private final CityRepository cityRepository;
    private final StateRepository stateRepository;
    private final HubRepository hubRepository;

    public AirportServiceImpl(AirportRepository airportRepository, CityRepository cityRepository,
                               StateRepository stateRepository, HubRepository hubRepository) {
        this.airportRepository = airportRepository;
        this.cityRepository = cityRepository;
        this.stateRepository = stateRepository;
        this.hubRepository = hubRepository;
    }

    @Override
    public List<AirportDTO> getAllAirports() {
        return airportRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<AirportDTO> searchAirports(String query) {
        String q = query == null ? "" : query.trim();
        List<Airport> results = q.isEmpty()
                ? airportRepository.findAll()
                : airportRepository.findByAirportCodeContainingIgnoreCaseOrAirportNameContainingIgnoreCase(q, q);
        return results.stream().limit(q.isEmpty() ? 6 : Long.MAX_VALUE).map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public AirportDTO getAirportById(Long airportId) {
        return airportRepository.findById(airportId).map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Airport not found: " + airportId));
    }

    @Override
    @Transactional
    public AirportDTO createAirport(AirportRequest request) {
        if (airportRepository.existsByAirportCodeIgnoreCase(request.getAirportCode())) {
            throw new ApiException("An airport with this code already exists: " + request.getAirportCode(), 409);
        }
        Airport airport = new Airport();
        airport.setAirportName(request.getAirportName());
        airport.setAirportCode(request.getAirportCode());
        applyReferences(airport, request);
        return toDto(airportRepository.save(airport));
    }

    @Override
    @Transactional
    public AirportDTO updateAirport(Long airportId, AirportRequest request) {
        Airport airport = airportRepository.findById(airportId)
                .orElseThrow(() -> new ResourceNotFoundException("Airport not found: " + airportId));
        if (airportRepository.existsByAirportCodeIgnoreCaseAndAirportIdNot(request.getAirportCode(), airportId)) {
            throw new ApiException("An airport with this code already exists: " + request.getAirportCode(), 409);
        }
        airport.setAirportName(request.getAirportName());
        airport.setAirportCode(request.getAirportCode());
        applyReferences(airport, request);
        return toDto(airportRepository.save(airport));
    }

    @Override
    @Transactional
    public void deleteAirport(Long airportId) {
        if (!airportRepository.existsById(airportId)) {
            throw new ResourceNotFoundException("Airport not found: " + airportId);
        }
        airportRepository.deleteById(airportId);
    }

    // No relationships to attach — just app-level referential-integrity
    // checks (each id must resolve to a real row) before the plain id
    // columns are set. This is the only "enforcement" these logical
    // references get, since there's no database-level FK constraint.
    private void applyReferences(Airport airport, AirportRequest request) {
        if (!cityRepository.existsById(request.getCityId())) {
            throw new ResourceNotFoundException("City not found: " + request.getCityId());
        }
        if (!stateRepository.existsById(request.getStateId())) {
            throw new ResourceNotFoundException("State not found: " + request.getStateId());
        }
        if (!hubRepository.existsById(request.getHubId())) {
            throw new ResourceNotFoundException("Hub not found: " + request.getHubId());
        }
        airport.setCityId(request.getCityId());
        airport.setStateId(request.getStateId());
        airport.setHubId(request.getHubId());
    }

    private AirportDTO toDto(Airport a) {
        return new AirportDTO(a.getAirportId(), a.getAirportCode(), a.getAirportName(),
                a.getCityId(), a.getStateId(), a.getHubId());
    }
}
