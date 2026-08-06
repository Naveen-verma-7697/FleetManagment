package com.fleman.controller;

import com.fleman.dto.CarDTO;
import com.fleman.dto.CarTypeDTO;
import com.fleman.service.VehicleService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping("/car-types")
    public ResponseEntity<List<CarTypeDTO>> getCarTypes(
            @RequestParam(required = false) Long hubId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime pickupDatetime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime returnDatetime) {
        return ResponseEntity.ok(vehicleService.getCarTypesForHub(hubId, pickupDatetime, returnDatetime));
    }

    @GetMapping("/cars/available")
    public ResponseEntity<List<CarDTO>> getAvailableCars(
            @RequestParam(required = false) Long hubId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime pickupDatetime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime returnDatetime) {
        return ResponseEntity.ok(vehicleService.getAvailableCars(hubId, pickupDatetime, returnDatetime));
    }

    @GetMapping("/cars/{carId}")
    public ResponseEntity<CarDTO> getCarById(@PathVariable Long carId) {
        return ResponseEntity.ok(vehicleService.getCarById(carId));
    }
}
