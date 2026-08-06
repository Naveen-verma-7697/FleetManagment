package com.fleman.service;

import com.fleman.dto.BookingResponseDTO;
import com.fleman.dto.CarDTO;
import com.fleman.dto.CarTypeAvailabilityDTO;
import com.fleman.dto.HandoverRequest;
import com.fleman.dto.InvoiceResponseDTO;
import com.fleman.dto.ProcessReturnRequest;

import java.util.List;

public interface StaffService {
    BookingResponseDTO handoverVehicle(String confirmationNo, HandoverRequest request);
    InvoiceResponseDTO processReturn(String confirmationNo, ProcessReturnRequest request);
    List<BookingResponseDTO> getAllBookings();
    List<CarTypeAvailabilityDTO> getDashboard(Long hubId);
    // Every car of this booking's reserved type, at its pickup hub, that's
    // physically AVAILABLE right now — the hand-over screen's picker list.
    List<CarDTO> getAvailableCarsForHandover(String confirmationNo);
}
