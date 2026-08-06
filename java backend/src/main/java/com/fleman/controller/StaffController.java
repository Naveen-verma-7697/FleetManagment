package com.fleman.controller;

import com.fleman.dto.BookingResponseDTO;
import com.fleman.dto.CarDTO;
import com.fleman.dto.CarTypeAvailabilityDTO;
import com.fleman.dto.HandoverRequest;
import com.fleman.dto.InvoiceResponseDTO;
import com.fleman.dto.ProcessReturnRequest;
import com.fleman.service.StaffService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    // The hand-over screen's car picker: every car of this booking's
    // reserved category, at its pickup hub, that's physically free right
    // now. Called as staff look up a confirmation number, before they pick
    // which one to actually hand over.
    @GetMapping("/bookings/{confirmationNo}/available-cars")
    public ResponseEntity<List<CarDTO>> getAvailableCarsForHandover(@PathVariable String confirmationNo) {
        return ResponseEntity.ok(staffService.getAvailableCarsForHandover(confirmationNo));
    }

    @PostMapping("/bookings/{confirmationNo}/handover")
    public ResponseEntity<BookingResponseDTO> handoverVehicle(
            @PathVariable String confirmationNo, @RequestBody(required = false) HandoverRequest request) {
        return ResponseEntity.ok(staffService.handoverVehicle(confirmationNo, request != null ? request : new HandoverRequest()));
    }

    @PostMapping("/bookings/{confirmationNo}/return")
    public ResponseEntity<InvoiceResponseDTO> processReturn(
            @PathVariable String confirmationNo, @RequestBody(required = false) ProcessReturnRequest request) {
        return ResponseEntity.ok(staffService.processReturn(confirmationNo, request != null ? request : new ProcessReturnRequest()));
    }

    // The staff booking page: every booking, with customer info and car
    // (type + registration number, confirmation no, etc.) already attached.
    @GetMapping("/bookings")
    public ResponseEntity<List<BookingResponseDTO>> getAllBookings() {
        return ResponseEntity.ok(staffService.getAllBookings());
    }

    // The staff dashboard: per car type, how many are available / handed
    // over / in maintenance right now. hubId is optional - omit it for a
    // fleet-wide view, or scope it to one hub.
    @GetMapping("/dashboard")
    public ResponseEntity<List<CarTypeAvailabilityDTO>> getDashboard(@RequestParam(required = false) Long hubId) {
        return ResponseEntity.ok(staffService.getDashboard(hubId));
    }
}
