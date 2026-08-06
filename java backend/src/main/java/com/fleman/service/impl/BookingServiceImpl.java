package com.fleman.service.impl;

import com.fleman.dto.*;
import com.fleman.entity.*;
import com.fleman.entity.BookingHeader.BookingStatus;
import com.fleman.entity.Car.CarStatus;
import com.fleman.exception.ApiException;
import com.fleman.exception.ResourceNotFoundException;
import com.fleman.repository.*;
import com.fleman.security.AuthenticatedUser;
import com.fleman.service.AddonService;
import com.fleman.service.BookingService;
import com.fleman.service.CustomerService;
import com.fleman.service.LocationService;
import com.fleman.service.VehicleService;
import com.fleman.util.RentalRateCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// A booking reserves a car TYPE at a hub for a date range, not any
// specific vehicle — the physical car is only assigned when staff hand it
// over (see StaffServiceImpl.handoverVehicle), which is also the moment
// header.carId stops being null. Availability here is therefore a
// capacity check (CarRepository.countByHubIdAndCarTypeIdAndStatusNot) minus
// demand (BookingHeaderRepository.countOverlapping), not a check against
// any one Car row.
@Service
public class BookingServiceImpl implements BookingService {

    private static final List<BookingStatus> ACTIVE_STATUSES = List.of(BookingStatus.CONFIRMED, BookingStatus.ONGOING);

    private final BookingHeaderRepository bookingHeaderRepository;
    private final BookingDetailRepository bookingDetailRepository;
    private final CarRepository carRepository;
    private final CarTypeRepository carTypeRepository;
    private final CustomerRepository customerRepository;
    private final HubRepository hubRepository;
    private final AddonRepository addonRepository;

    // Reusing the sibling services' own DTO builders (getCarById, getHubById,
    // etc.) instead of duplicating that mapping logic here — one source of
    // truth per entity type.
    private final VehicleService vehicleService;
    private final CustomerService customerService;
    private final LocationService locationService;
    private final AddonService addonService;
    private final EmailService emailService;
    

    public BookingServiceImpl(BookingHeaderRepository bookingHeaderRepository,
                               BookingDetailRepository bookingDetailRepository,
                               CarRepository carRepository,
                               CarTypeRepository carTypeRepository,
                               CustomerRepository customerRepository,
                               HubRepository hubRepository,
                               AddonRepository addonRepository,
                               VehicleService vehicleService,
                               CustomerService customerService,
                               LocationService locationService,
                               AddonService addonService,
                               EmailService emailService
                               ) {
        this.bookingHeaderRepository = bookingHeaderRepository;
        this.bookingDetailRepository = bookingDetailRepository;
        this.carRepository = carRepository;
        this.carTypeRepository = carTypeRepository;
        this.customerRepository = customerRepository;
        this.hubRepository = hubRepository;
        this.addonRepository = addonRepository;
        this.vehicleService = vehicleService;
        this.customerService = customerService;
        this.locationService = locationService;
        this.addonService = addonService;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public BookingResponseDTO createBooking(CreateBookingRequest request) {
    	Long customerId;

    	if (AuthenticatedUser.isAuthenticated()) {

    	    if ("STAFF".equals(AuthenticatedUser.getCurrentRole())) {

    	        if (request.getCustomerId() == null) {
    	            throw new ApiException("Customer is required.", 400);
    	        }

    	        customerId = request.getCustomerId();

    	    } else {

    	        customerId = AuthenticatedUser.getCurrentUserId();
    	    }

    	} else {

    	    if (request.getCustomerId() == null) {
    	        throw new ApiException("Customer is required.", 400);
    	    }

    	    customerId = request.getCustomerId();
    	}
    	    
        CarType carType = carTypeRepository.findById(request.getCarTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Car type not found: " + request.getCarTypeId()));
        if (!request.getReturnDatetime().isAfter(request.getPickupDatetime())) {
            throw new ApiException("Return date must be after pickup date", 400);
        }
        if (!customerRepository.existsById(request.getCustomerId())) {
            throw new ResourceNotFoundException("Customer not found: " + request.getCustomerId());
        }
        Long dropHubId = request.getDropHubId() != null ? request.getDropHubId() : request.getPickupHubId();
        if (!hubRepository.existsById(request.getPickupHubId())) {
            throw new ResourceNotFoundException("Hub not found: " + request.getPickupHubId());
        }
        if (!hubRepository.existsById(dropHubId)) {
            throw new ResourceNotFoundException("Hub not found: " + dropHubId);
        }
        assertCategoryHasCapacity(request.getPickupHubId(), request.getCarTypeId(),
                request.getPickupDatetime(), request.getReturnDatetime(), null);

        BookingHeader header = new BookingHeader();
        header.setConfirmationNo(generateConfirmationNo());
        header.setBookingDate(LocalDateTime.now());
       // header.setCustomerId(request.getCustomerId());
        header.setCustomerId(customerId);
        header.setCarTypeId(carType.getCarTypeId());
        header.setCarId(null); // assigned later, at staff hand-over
        header.setPickupHubId(request.getPickupHubId());
        header.setDropHubId(dropHubId);
        header.setPickupDatetime(request.getPickupDatetime());
        header.setReturnDatetime(request.getReturnDatetime());
        header.setBookingStatus(BookingStatus.CONFIRMED);
        header.setRemarks(request.getRemarks() != null ? request.getRemarks() : "");

        int days = daysBetween(request.getPickupDatetime(), request.getReturnDatetime());
        double rentalAmount = RentalRateCalculator.calculateRentalAmount(carType, days);

        header = bookingHeaderRepository.save(header);

        double addonAmount = 0;
        if (request.getAddons() != null) {
            for (AddonLineRequest line : request.getAddons()) {
                Addon addon = addonRepository.findById(line.getAddonId()).orElse(null);
                if (addon == null) continue;
                // Subtotal is computed server-side from the addon's current
                // rate — the client never gets to submit its own price.
                double subtotal = addon.getDailyRate() * line.getQuantity() * days;
                BookingDetail detail = new BookingDetail();
                detail.setBookingId(header.getBookingId());
                detail.setAddonId(addon.getAddonId());
                detail.setAddonRate(addon.getDailyRate());
                detail.setQuantity(line.getQuantity());
                detail.setSubtotal(subtotal);
                bookingDetailRepository.save(detail);
                addonAmount += subtotal;
            }
        }
        
//        header.setEstimatedAmount(rentalAmount + addonAmount);
//        header = bookingHeaderRepository.save(header);
//    	
//        return toResponseDto(header);
        
        header.setEstimatedAmount(rentalAmount + addonAmount);
        header = bookingHeaderRepository.save(header);

        /* -------------------- SEND EMAIL -------------------- */

        try {

            Customer customer = customerRepository
                    .findById(header.getCustomerId())
                    .orElse(null);

            if (customer != null) {

                String body = """
                        Dear %s,

                        Your booking has been confirmed successfully.

                        Booking Details
                        -------------------------
                        Booking No : %s
                        Vehicle Type : %s
                        Pickup Date : %s
                        Return Date : %s
                        Total Amount : ₹%.2f

                        Thank you for choosing Fleeman.

                        Regards,
                        Fleeman Team
                        """
                        .formatted(
                                customer.getFullName(),
                                header.getConfirmationNo(),
                                carType.getCarTypeName(),     // <--i Changed this name 
                                header.getPickupDatetime(),
                                header.getReturnDatetime(),
                                header.getEstimatedAmount()
                        );

                emailService.sendEmail(
                        customer.getEmail(),
                        "Booking Confirmation - Fleeman",
                        body
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        /* ---------------------------------------------------- */

        return toResponseDto(header);
        
    }

    @Override
    public BookingResponseDTO getBookingByConfirmation(String confirmationNo) {
        BookingHeader header = findHeader(confirmationNo);
        validateBookingAccess(header);
        return toResponseDto(header);
    }

    @Override
    public BookingResponseDTO getLastBookingForCustomer(Long customerId) {
        List<BookingHeader> mine = bookingHeaderRepository
                .findByCustomerIdAndBookingStatusNotOrderByBookingDateDesc(customerId, BookingStatus.CANCELLED);
        return mine.isEmpty() ? null : toResponseDto(mine.get(0));
    }

    @Override
    public List<BookingResponseDTO> getBookingsForCustomer(Long customerId) {
        // Same query as getLastBookingForCustomer, minus the .get(0) — used
        // by the "modify booking" flow's booking picker, which needs every
        // one of the customer's bookings, not just the most recent.
        return bookingHeaderRepository
                .findByCustomerIdAndBookingStatusNotOrderByBookingDateDesc(customerId, BookingStatus.CANCELLED)
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingResponseDTO modifyBooking(String confirmationNo, ModifyBookingRequest request) {
        BookingHeader header = findHeader(confirmationNo);
        //change
        validateBookingAccess(header);

        // Once a real car has been handed to the customer, the pickup leg is
        // history — only the still-pending return leg (drop hub / return
        // datetime) may still change. Mirrors the carTypeId guard below.
        boolean pickupHubChanged = request.getPickupHubId() != null
                && !request.getPickupHubId().equals(header.getPickupHubId());
        boolean pickupTimeChanged = request.getPickupDatetime() != null
                && !request.getPickupDatetime().equals(header.getPickupDatetime());
        if ((pickupHubChanged || pickupTimeChanged) && header.getCarId() != null) {
            throw new ApiException("Vehicle has already been handed over — pickup location and time can no longer be changed", 409);
        }

        if (request.getPickupHubId() != null) {
            if (!hubRepository.existsById(request.getPickupHubId())) {
                throw new ResourceNotFoundException("Hub not found: " + request.getPickupHubId());
            }
            header.setPickupHubId(request.getPickupHubId());
        }
        if (request.getDropHubId() != null) {
            if (!hubRepository.existsById(request.getDropHubId())) {
                throw new ResourceNotFoundException("Hub not found: " + request.getDropHubId());
            }
            header.setDropHubId(request.getDropHubId());
        }
        if (request.getPickupDatetime() != null) header.setPickupDatetime(request.getPickupDatetime());
        if (request.getReturnDatetime() != null) header.setReturnDatetime(request.getReturnDatetime());
        if (request.getRemarks() != null) header.setRemarks(request.getRemarks());

        // Changing the reserved category: only while no real car has been
        // handed over yet — once a customer is physically holding a
        // vehicle, its category can't retroactively change.
        boolean typeChanged = request.getCarTypeId() != null && !request.getCarTypeId().equals(header.getCarTypeId());
        if (typeChanged) {
            if (header.getCarId() != null) {
                throw new ApiException("Vehicle has already been handed over — category can no longer be changed", 409);
            }
            if (!carTypeRepository.existsById(request.getCarTypeId())) {
                throw new ResourceNotFoundException("Car type not found: " + request.getCarTypeId());
            }
            header.setCarTypeId(request.getCarTypeId());
        }

        // Any change to hub, dates, or category needs a fresh capacity
        // check against the booking's own final values — excluding this
        // same booking from the demand count, so it isn't blocked by its
        // own existing reservation. Skipped once a specific car is already
        // assigned, since that booking no longer competes for open
        // category capacity the same way.
        boolean needsRecheck = typeChanged || request.getPickupHubId() != null
                || request.getPickupDatetime() != null || request.getReturnDatetime() != null;
        if (needsRecheck && header.getCarId() == null) {
            assertCategoryHasCapacity(header.getPickupHubId(), header.getCarTypeId(),
                    header.getPickupDatetime(), header.getReturnDatetime(), header.getBookingId());
        }

        // Replacing the add-on lines: null on the request means "the
        // customer didn't touch this step, leave them as they are"; an
        // empty list means "every add-on was removed" - ordinary partial-
        // patch semantics, just applied to a whole child table instead of
        // a single column. Once a real car has been handed over, the
        // add-on set is locked too — mirrors the pickup-hub/pickupDatetime
        // guard above.
        if (request.getAddons() != null && header.getCarId() != null) {
            throw new ApiException("Vehicle has already been handed over — add-ons can no longer be changed", 409);
        }
        int days = daysBetween(header.getPickupDatetime(), header.getReturnDatetime());
        double addonAmount;
        if (request.getAddons() != null) {
            bookingDetailRepository.deleteAll(bookingDetailRepository.findByBookingId(header.getBookingId()));
            double total = 0;
            for (AddonLineRequest line : request.getAddons()) {
                Addon addon = addonRepository.findById(line.getAddonId()).orElse(null);
                if (addon == null) continue;
                double subtotal = addon.getDailyRate() * line.getQuantity() * days;
                BookingDetail detail = new BookingDetail();
                detail.setBookingId(header.getBookingId());
                detail.setAddonId(addon.getAddonId());
                detail.setAddonRate(addon.getDailyRate());
                detail.setQuantity(line.getQuantity());
                detail.setSubtotal(subtotal);
                bookingDetailRepository.save(detail);
                total += subtotal;
            }
            addonAmount = total;
        } else {
            addonAmount = bookingDetailRepository.findByBookingId(header.getBookingId())
                    .stream().mapToDouble(BookingDetail::getSubtotal).sum();
        }

        CarType currentCarType = carTypeRepository.findById(header.getCarTypeId()).orElse(null);
        double rentalAmount = currentCarType != null
                ? RentalRateCalculator.calculateRentalAmount(currentCarType, days) : 0.0;
        header.setEstimatedAmount(rentalAmount + addonAmount);

        return toResponseDto(bookingHeaderRepository.save(header));
    }

    @Override
    @Transactional
    public BookingResponseDTO cancelBooking(String confirmationNo) {
        BookingHeader header = findHeader(confirmationNo);
        //change it
        validateBookingAccess(header);
        header.setBookingStatus(BookingStatus.CANCELLED);
        bookingHeaderRepository.save(header);

        // A car has only ever been touched if it was actually handed over
        // (header.carId set) — release it back to the fleet in that case.
        // Otherwise there's nothing to release: the booking only ever held
        // category capacity, which frees itself the moment the status
        // above stops being CONFIRMED/ONGOING.
        if (header.getCarId() != null) {
            carRepository.findById(header.getCarId()).ifPresent(car -> {
                car.setStatus(CarStatus.AVAILABLE);
                car.setAvailable(true);
                carRepository.save(car);
            });
        }

        return toResponseDto(header);
    }

    // Capacity (physical fleet of this type at this hub, minus maintenance)
    // minus demand (other CONFIRMED/ONGOING bookings of this type at this
    // hub whose dates overlap) must leave at least one car free, or the
    // reservation is rejected. excludeBookingId lets a booking re-check
    // itself without counting its own existing reservation as competition.
    private void assertCategoryHasCapacity(Long hubId, Long carTypeId, LocalDateTime pickupDatetime,
                                            LocalDateTime returnDatetime, Long excludeBookingId) {
        long capacity = carRepository.countByHubIdAndCarTypeIdAndStatusNot(hubId, carTypeId, CarStatus.UNDER_MAINTENANCE);
        long overlapping = bookingHeaderRepository.countOverlapping(
                hubId, carTypeId, pickupDatetime, returnDatetime, ACTIVE_STATUSES, excludeBookingId);
        if (capacity - overlapping <= 0) {
            throw new ApiException("That car type is no longer available at this hub for these dates", 409);
        }
    }

    private BookingHeader findHeader(String confirmationNo) {
        return bookingHeaderRepository.findByConfirmationNoIgnoreCase(confirmationNo)
                .orElseThrow(() -> new ResourceNotFoundException("No booking found for that confirmation number"));
    }

    // Robust against deleted rows — scans existing confirmation numbers for
    // the highest "WDR-####" suffix rather than trusting a running count,
    // which a delete could throw out of sync.
    
    
    
//    private String generateConfirmationNo() {
//        int max = bookingHeaderRepository.findAll().stream()
//                .map(BookingHeader::getConfirmationNo)
//                .filter(no -> no != null && no.startsWith("WDR-"))
//                .mapToInt(no -> {
//                    try {
//                        return Integer.parseInt(no.substring(4));
//                    } catch (NumberFormatException e) {
//                        return 2894;
//                    }
//                })
//                .max().orElse(2894);
//        return "WDR-" + (max + 1);
//    }
//    
    private static final String CONFIRMATION_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String CONFIRMATION_DIGITS = "0123456789";
    private static final int CONFIRMATION_LETTER_COUNT = 3;
    private static final int CONFIRMATION_DIGIT_COUNT = 5;
    private static final java.security.SecureRandom RANDOM = new java.security.SecureRandom();

    // Fully random each time (3 letters + 5 digits, e.g. "QXR48213") — no
    // sequence, no fixed prefix. Re-rolls on the rare chance of a collision
    // with an existing confirmation number.
    private String generateConfirmationNo() {
        String candidate;
        do {
            StringBuilder sb = new StringBuilder(CONFIRMATION_LETTER_COUNT + CONFIRMATION_DIGIT_COUNT);
            for (int i = 0; i < CONFIRMATION_LETTER_COUNT; i++) {
                sb.append(CONFIRMATION_LETTERS.charAt(RANDOM.nextInt(CONFIRMATION_LETTERS.length())));
            }
            for (int i = 0; i < CONFIRMATION_DIGIT_COUNT; i++) {
                sb.append(CONFIRMATION_DIGITS.charAt(RANDOM.nextInt(CONFIRMATION_DIGITS.length())));
            }
            candidate = sb.toString();
        } while (bookingHeaderRepository.existsByConfirmationNoIgnoreCase(candidate));
        return candidate;
    }
    

    private int daysBetween(LocalDateTime pickup, LocalDateTime returnDt) {
        long hours = Duration.between(pickup, returnDt).toHours();
        long fullDays = (long) Math.ceil(hours / 24.0);
        return (int) Math.max(1, fullDays);
    }
  //private helper method checks, if it is the staff and if not customer then access denied
    //authorization logic, can change in future ex. if need to add new role="ADMIN"
    
    private void validateBookingAccess(BookingHeader booking) {

        String role = AuthenticatedUser.getCurrentRole();

        if ("STAFF".equals(role)) {
            return;
        }

        Long customerId = AuthenticatedUser.getCurrentUserId();

        if (!booking.getCustomerId().equals(customerId)) {
            throw new ApiException("Access denied.", 403);
        }
    }
    // The Java equivalent of the frontend mock's enrich(header) function —
    // every booking-related endpoint returns this same nested shape. No
    // entity relationships are traversed here — every nested object is
    // fetched by its plain id column through the owning service. carType
    // comes straight from header.carTypeId so it's always present; car is
    // only populated once header.carId is set (i.e. after hand-over).
    private BookingResponseDTO toResponseDto(BookingHeader header) {
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setBookingId(header.getBookingId());
        dto.setConfirmationNo(header.getConfirmationNo());
        dto.setBookingDate(header.getBookingDate());
        dto.setCustomerId(header.getCustomerId());
        dto.setCarId(header.getCarId());
        dto.setPickupHubId(header.getPickupHubId());
        dto.setDropHubId(header.getDropHubId());
        dto.setPickupDatetime(header.getPickupDatetime());
        dto.setReturnDatetime(header.getReturnDatetime());
        dto.setActualHandoverDatetime(header.getActualHandoverDatetime());
        dto.setActualReturnDatetime(header.getActualReturnDatetime());
        dto.setHandoverFuelLevel(header.getHandoverFuelLevel());
        dto.setBookingStatus(header.getBookingStatus() != null ? header.getBookingStatus().name() : null);
        dto.setEstimatedAmount(header.getEstimatedAmount());
        dto.setRemarks(header.getRemarks());

        dto.setCarType(vehicleService.getCarTypeById(header.getCarTypeId()));
        if (header.getCarId() != null) {
            dto.setCar(vehicleService.getCarById(header.getCarId()));
        }
        dto.setCustomer(customerService.getCustomerById(header.getCustomerId()));
        dto.setPickupHub(locationService.getHubById(header.getPickupHubId()));
        dto.setDropHub(locationService.getHubById(header.getDropHubId()));

        List<AddonDTO> allAddons = addonService.getAddons();
        List<BookingDetailDTO> lines = bookingDetailRepository.findByBookingId(header.getBookingId())
                .stream()
                .map(detail -> {
                    BookingDetailDTO lineDto = new BookingDetailDTO();
                    lineDto.setBookingDetailId(detail.getBookingDetailId());
                    lineDto.setBookingId(header.getBookingId());
                    lineDto.setAddonId(detail.getAddonId());
                    lineDto.setAddonRate(detail.getAddonRate());
                    lineDto.setQuantity(detail.getQuantity());
                    lineDto.setSubtotal(detail.getSubtotal());
                    lineDto.setAddon(allAddons.stream()
                            .filter(a -> a.getAddonId().equals(detail.getAddonId()))
                            .findFirst().orElse(null));
                    return lineDto;
                })
                .collect(Collectors.toList());
        dto.setAddonLines(lines);

        return dto;
    }
}
