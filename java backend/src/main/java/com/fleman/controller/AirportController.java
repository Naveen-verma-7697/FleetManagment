package com.fleman.controller;

import com.fleman.dto.AirportDTO;
import com.fleman.dto.AirportRequest;
import com.fleman.service.AirportService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// The complete Airport Master API. Every route lives under /api/airports —
// the literal "/search" segment is declared before "/{airportId}" so it's
// never mistaken for a numeric id (Spring matches the literal path first
// either way, but the ordering here is for readability).
@RestController
@RequestMapping("/api/airports")
public class AirportController {

    private final AirportService airportService;

    public AirportController(AirportService airportService) {
        this.airportService = airportService;
    }

    @GetMapping
    public ResponseEntity<List<AirportDTO>> getAllAirports() {
        return ResponseEntity.ok(airportService.getAllAirports());
    }

    @GetMapping("/search")
    public ResponseEntity<List<AirportDTO>> searchAirports(@RequestParam(required = false) String q) {
        return ResponseEntity.ok(airportService.searchAirports(q));
    }

    @GetMapping("/{airportId}")
    public ResponseEntity<AirportDTO> getAirportById(@PathVariable Long airportId) {
        return ResponseEntity.ok(airportService.getAirportById(airportId));
    }

    @PostMapping
    public ResponseEntity<AirportDTO> createAirport(@Valid @RequestBody AirportRequest request) {
        return ResponseEntity.ok(airportService.createAirport(request));
    }

    @PutMapping("/{airportId}")
    public ResponseEntity<AirportDTO> updateAirport(
            @PathVariable Long airportId, @Valid @RequestBody AirportRequest request) {
        return ResponseEntity.ok(airportService.updateAirport(airportId, request));
    }

    @DeleteMapping("/{airportId}")
    public ResponseEntity<Void> deleteAirport(@PathVariable Long airportId) {
        airportService.deleteAirport(airportId);
        return ResponseEntity.noContent().build();
    }
}
