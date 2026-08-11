package com.fleman.controller;
import com.fleman.dto.BookingResponseDTO;
import com.fleman.dto.CreateBookingRequest;
import com.fleman.dto.ModifyBookingRequest;
import com.fleman.security.AuthenticatedUser;
import com.fleman.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;

    private static final Logger log = LoggerFactory.getLogger(BookingController.class);
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }
    @PostMapping
    public ResponseEntity<BookingResponseDTO> createBooking(@Valid @RequestBody CreateBookingRequest request) {
        log.debug("createBooking request: {}", request);
        BookingResponseDTO response = bookingService.createBooking(request);
        log.info("Booking created, confirmationNo={}", response.getConfirmationNo());
        return ResponseEntity.ok(response);
    }
    @GetMapping("/confirmation/{confirmationNo}")
    public ResponseEntity<BookingResponseDTO> getByConfirmation(@PathVariable String confirmationNo) {
        log.debug("Fetching booking by confirmationNo={}", confirmationNo);
        return ResponseEntity.ok(bookingService.getBookingByConfirmation(confirmationNo));
    }
    // change
    @GetMapping("/me")
    public ResponseEntity<BookingResponseDTO> getLastForCustomer() {
        Long userId = AuthenticatedUser.getCurrentUserId();
        log.debug("Fetching last booking for userId={}", userId);
        return ResponseEntity.ok(bookingService.getLastBookingForCustomer(userId));
    }
    // Every one of the customer's bookings, not just the last one - backs
    // the "modify booking" page's picker, where a logged-in customer sees
    // all their bookings and chooses which one to change.
//    @GetMapping
//    public ResponseEntity<List<BookingResponseDTO>> getBookingsForCustomer(@RequestParam Long customerId) {
//        return ResponseEntity.ok(bookingService.getBookingsForCustomer(customerId));
//    }
    @GetMapping
    public ResponseEntity<List<BookingResponseDTO>> getMyBookings() {
        Long userId = AuthenticatedUser.getCurrentUserId();
        log.debug("Fetching all bookings for userId={}", userId);
        return ResponseEntity.ok(bookingService.getBookingsForCustomer(userId));
    }
    @PutMapping("/confirmation/{confirmationNo}")
    public ResponseEntity<BookingResponseDTO> modifyBooking(

            @PathVariable String confirmationNo, @RequestBody ModifyBookingRequest request) {
        log.info("Modifying booking confirmationNo={}", confirmationNo);
        return ResponseEntity.ok(bookingService.modifyBooking(confirmationNo, request));
    }
    @DeleteMapping("/confirmation/{confirmationNo}")
    public ResponseEntity<BookingResponseDTO> cancelBooking(@PathVariable String confirmationNo) {
        log.warn("Cancelling booking confirmationNo={}", confirmationNo);
        return ResponseEntity.ok(bookingService.cancelBooking(confirmationNo));
    }
}