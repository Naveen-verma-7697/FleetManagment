package com.fleman.controller;

import com.fleman.dto.CityDTO;
import com.fleman.dto.HubDTO;
import com.fleman.dto.StateDTO;
import com.fleman.service.LocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping("/states")
    public ResponseEntity<List<StateDTO>> getStates() {
        return ResponseEntity.ok(locationService.getStates());
    }

    @GetMapping("/states/{stateId}")
    public ResponseEntity<StateDTO> getStateById(@PathVariable Long stateId) {
        return ResponseEntity.ok(locationService.getStateById(stateId));
    }

    @GetMapping("/cities")
    public ResponseEntity<List<CityDTO>> getCitiesByState(@RequestParam(required = false) Long stateId) {
        return ResponseEntity.ok(locationService.getCitiesByState(stateId));
    }

    @GetMapping("/cities/{cityId}")
    public ResponseEntity<CityDTO> getCityById(@PathVariable Long cityId) {
        return ResponseEntity.ok(locationService.getCityById(cityId));
    }

    @GetMapping("/hubs")
    public ResponseEntity<List<HubDTO>> getAllHubs() {
        return ResponseEntity.ok(locationService.getAllHubs());
    }

    @GetMapping("/hubs/{hubId}")
    public ResponseEntity<HubDTO> getHubById(@PathVariable Long hubId) {
        return ResponseEntity.ok(locationService.getHubById(hubId));
    }

    @GetMapping("/hubs/by-city/{cityId}")
    public ResponseEntity<HubDTO> findHubForCity(@PathVariable Long cityId) {
        return ResponseEntity.ok(locationService.findHubForCity(cityId));
    }

    @GetMapping("/hubs/by-airport/{airportId}")
    public ResponseEntity<HubDTO> findHubForAirport(@PathVariable Long airportId) {
        return ResponseEntity.ok(locationService.findHubForAirport(airportId));
    }
}
