package com.fleman.service;

import com.fleman.dto.BookingResponseDTO;
import com.fleman.dto.CreateBookingRequest;
import com.fleman.dto.ModifyBookingRequest;

import java.util.List;

public interface BookingService {
    BookingResponseDTO createBooking(CreateBookingRequest request);
    BookingResponseDTO getBookingByConfirmation(String confirmationNo);
    BookingResponseDTO getLastBookingForCustomer(Long customerId);
    List<BookingResponseDTO> getBookingsForCustomer(Long customerId);
    BookingResponseDTO modifyBooking(String confirmationNo, ModifyBookingRequest request);
    BookingResponseDTO cancelBooking(String confirmationNo);
}
