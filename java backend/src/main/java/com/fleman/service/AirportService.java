package com.fleman.service;

import com.fleman.dto.AirportDTO;
import com.fleman.dto.AirportRequest;

import java.util.List;

// The complete Airport Master API's business logic, deliberately kept
// separate from LocationService (which owns State/City/Hub read-lookups)
// so this module can be dropped into another project on its own — the
// only other entities it needs are City, State, and Hub, which the DB
// design's Airport Master sheet already lists as its foreign keys.
public interface AirportService {
    List<AirportDTO> getAllAirports();
    List<AirportDTO> searchAirports(String query);
    AirportDTO getAirportById(Long airportId);
    AirportDTO createAirport(AirportRequest request);
    AirportDTO updateAirport(Long airportId, AirportRequest request);
    void deleteAirport(Long airportId);
}
