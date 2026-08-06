package com.fleman.service.impl;

import com.fleman.dto.BookingResponseDTO;
import com.fleman.dto.CarDTO;
import com.fleman.dto.CarTypeAvailabilityDTO;
import com.fleman.dto.HandoverRequest;
import com.fleman.dto.InvoiceResponseDTO;
import com.fleman.dto.ProcessReturnRequest;
import com.fleman.entity.*;
import com.fleman.entity.BookingHeader.BookingStatus;
import com.fleman.entity.Car.CarStatus;
import com.fleman.entity.InvoiceHeader.PaymentStatus;
import com.fleman.exception.ApiException;
import com.fleman.exception.ResourceNotFoundException;
import com.fleman.repository.*;
import com.fleman.service.BookingService;
import com.fleman.service.InvoicePdfService;
import com.fleman.service.StaffService;
import com.fleman.service.VehicleService;
import com.fleman.util.RentalRateCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.fleman.service.InvoicePdfService;
@Service
public class StaffServiceImpl implements StaffService {

    // Fuel level is stored in quarters of a tank: 25/50/75/100.
    private static final int FUEL_LEVEL_PER_QUARTER = 25;
    private static final double FUEL_CHARGE_PER_QUARTER = 2.0;

    private final BookingHeaderRepository bookingHeaderRepository;
    private final BookingDetailRepository bookingDetailRepository;
    private final CarRepository carRepository;
    private final CarTypeRepository carTypeRepository;
    private final CustomerRepository customerRepository;
    private final AddonRepository addonRepository;
    private final InvoiceHeaderRepository invoiceHeaderRepository;
    private final InvoiceDetailRepository invoiceDetailRepository;
    private final BookingService bookingService;
    private final VehicleService vehicleService;
    private final EmailService emailService;
    private final InvoicePdfService invoicePdfService;

    public StaffServiceImpl(BookingHeaderRepository bookingHeaderRepository,
                             BookingDetailRepository bookingDetailRepository,
                             CarRepository carRepository,
                             CarTypeRepository carTypeRepository,
                             CustomerRepository customerRepository,
                             AddonRepository addonRepository,
                             InvoiceHeaderRepository invoiceHeaderRepository,
                             InvoiceDetailRepository invoiceDetailRepository,
                             BookingService bookingService,
                             VehicleService vehicleService,
                             EmailService emailService,
                             InvoicePdfService invoicePdfService
                             ) {
        this.bookingHeaderRepository = bookingHeaderRepository;
        this.bookingDetailRepository = bookingDetailRepository;
        this.carRepository = carRepository;
        this.carTypeRepository = carTypeRepository;
        this.customerRepository = customerRepository;
        this.addonRepository = addonRepository;
        this.invoiceHeaderRepository = invoiceHeaderRepository;
        this.invoiceDetailRepository = invoiceDetailRepository;
        this.bookingService = bookingService;
        this.vehicleService = vehicleService;
        this.emailService = emailService;
        this.invoicePdfService = invoicePdfService;
    }

    @Override
    @Transactional
    public BookingResponseDTO handoverVehicle(String confirmationNo, HandoverRequest request) {
        BookingHeader header = findHeader(confirmationNo);

        if (header.getCarId() != null) {
            throw new ApiException("This booking already has a vehicle handed over", 409);
        }
        if (request.getCarId() == null) {
            throw new ApiException("Pick a vehicle to hand over", 400);
        }

        // The car staff picked must actually be one that fits this
        // booking — right hub, right reserved category, and physically
        // free right now. This is the "minus one from the category" step:
        // once assigned, this car's status leaves it out of every other
        // available-count computation (dashboard, vehicle-selection page,
        // this same picker for the next booking) until it's returned.
        Car car = carRepository.findById(request.getCarId())
                .orElseThrow(() -> new ResourceNotFoundException("Car not found: " + request.getCarId()));
        if (!car.getHubId().equals(header.getPickupHubId())) {
            throw new ApiException("That vehicle isn't at this booking's pickup hub", 409);
        }
        if (!car.getCarTypeId().equals(header.getCarTypeId())) {
            throw new ApiException("That vehicle isn't the reserved category for this booking", 409);
        }
        if (car.getStatus() != CarStatus.AVAILABLE) {
            throw new ApiException("That vehicle is no longer available", 409);
        }

        car.setStatus(CarStatus.BOOKED);
        car.setAvailable(false);
        if (request.getFuelStatus() != null) car.setFuelLevel(request.getFuelStatus());
        carRepository.save(car);

        header.setCarId(car.getCarId());
        header.setBookingStatus(BookingStatus.ONGOING);
        header.setActualHandoverDatetime(LocalDateTime.now());
        // Snapshot onto the booking, not just the car — car.fuelLevel gets
        // overwritten again at return, so this is the only place the
        // return-time fuel-shortfall charge can still read it from.
        header.setHandoverFuelLevel(car.getFuelLevel());
        if (request.getNotes() != null && !request.getNotes().isBlank()) {
            String existing = header.getRemarks() != null ? header.getRemarks() : "";
            header.setRemarks((existing + " | Handover: " + request.getNotes()).trim());
        }
        bookingHeaderRepository.save(header);

        return bookingService.getBookingByConfirmation(confirmationNo);
    }

    @Override
    @Transactional
    public InvoiceResponseDTO processReturn(String confirmationNo, ProcessReturnRequest request) {
        BookingHeader header = findHeader(confirmationNo);
        if (header.getCarId() == null) {
            throw new ApiException("This booking hasn't been handed over yet — nothing to return", 409);
        }
        Car car = carRepository.findById(header.getCarId())
                .orElseThrow(() -> new ResourceNotFoundException("Car not found: " + header.getCarId()));
        CarType carType = carTypeRepository.findById(car.getCarTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Car type not found: " + car.getCarTypeId()));
        Customer customer = customerRepository.findById(header.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + header.getCustomerId()));
        List<BookingDetail> lines = bookingDetailRepository.findByBookingId(header.getBookingId());

        // Bill against what actually happened at the counter — when this
        // car was handed over and the moment staff are processing the
        // return right now — not the originally booked pickup/return
        // window. Falls back to the planned pickup time only for the
        // (pre-migration) edge case where a handover happened before this
        // field existed and was never backfilled.
        LocalDateTime actualHandover = header.getActualHandoverDatetime() != null
                ? header.getActualHandoverDatetime() : header.getPickupDatetime();
        LocalDateTime actualReturn = LocalDateTime.now();
        header.setActualReturnDatetime(actualReturn);

        int days = daysBetween(actualHandover, actualReturn);
        double rentalAmount = RentalRateCalculator.calculateRentalAmount(carType, days);
        double addonAmount = lines.stream().mapToDouble(BookingDetail::getSubtotal).sum();
        double extraCharge = request.getExtraChargeAmount() != null ? request.getExtraChargeAmount() : 0.0;

        // Fuel shortfall: the tank is tracked in quarters (25/50/75/100).
        // Only charged when it comes back lower than it was handed over
        // with — e.g. handed over Full (100), returned at Half (50) is 2
        // quarters short, so 2 x FUEL_CHARGE_PER_QUARTER. Coming back with
        // the same or more fuel costs nothing (no credit for topping up).
        // car.getFuelLevel() here is still the hand-over-time reading —
        // it isn't overwritten to the return reading until further below.
        Integer handoverFuelLevel = header.getHandoverFuelLevel();
        Integer returnFuelLevel = request.getFuelStatus() != null ? request.getFuelStatus() : car.getFuelLevel();
        int fuelQuartersShort = (handoverFuelLevel != null && returnFuelLevel != null)
                ? Math.max(0, (handoverFuelLevel - returnFuelLevel) / FUEL_LEVEL_PER_QUARTER)
                : 0;
        double fuelCharge = fuelQuartersShort * FUEL_CHARGE_PER_QUARTER;

        double totalAmount = rentalAmount + addonAmount + extraCharge + fuelCharge;

        InvoiceHeader invoice = new InvoiceHeader();
        invoice.setInvoiceDate(LocalDateTime.now());
        invoice.setHandoverDatetime(actualHandover);
        invoice.setReturnDatetime(actualReturn);
        invoice.setHandoverFuelLevel(handoverFuelLevel);
        invoice.setReturnFuelLevel(returnFuelLevel);
        invoice.setFuelCharge(fuelCharge);
        invoice.setBookingId(header.getBookingId());
        invoice.setCustomerName(customer.getFullName());
        invoice.setPhone(customer.getPhone());
        invoice.setEmail(customer.getEmail());
        invoice.setVehicleNumber(car.getVehicleNumber());
        invoice.setPickupHub(header.getPickupHubId());
        invoice.setDropHub(header.getDropHubId());
        invoice.setRentalAmount(rentalAmount);
        invoice.setAddonAmount(addonAmount);
        invoice.setTotalAmount(totalAmount);
        invoice.setPaymentType(request.getPaymentType());
        // Assumes payment is settled at the return desk when a payment
        // type is given; otherwise left PENDING for a separate collection step.
        invoice.setPaymentStatus(request.getPaymentType() != null ? PaymentStatus.PAID : PaymentStatus.PENDING);
        invoice.setExtraMiles(request.getExtraMiles());
        invoice.setExtraChargeAmount(extraCharge);
        invoice.setDamageNotes(request.getDamageNotes());
        invoice.setDays(days);
        invoice = invoiceHeaderRepository.save(invoice);
        invoice.setPaymentReference("PAY-" + invoice.getInvoiceId() + "-" + String.valueOf(System.currentTimeMillis()).substring(9));
        invoice = invoiceHeaderRepository.save(invoice);

        for (BookingDetail line : lines) {
            InvoiceDetail detail = new InvoiceDetail();
            detail.setInvoiceId(invoice.getInvoiceId());
            // addonId is a plain logical reference — look up the current
            // name fresh rather than trusting anything cached on the line.
            detail.setAddonName(addonRepository.findById(line.getAddonId())
                    .map(Addon::getAddonName).orElse(""));
            detail.setQuantity(line.getQuantity());
            detail.setAddonRate(line.getAddonRate());
            detail.setSubtotal(line.getSubtotal());
            invoiceDetailRepository.save(detail);
        }

        header.setBookingStatus(BookingStatus.COMPLETED);
        bookingHeaderRepository.save(header);

        // The "add one back to the category" step — this car goes back to
        // AVAILABLE, so it counts again in the dashboard, the vehicle-
        // selection page's per-category count, and the next booking's
        // hand-over picker for this same type/hub.
        car.setStatus(CarStatus.AVAILABLE);
        car.setAvailable(true);
        if (request.getFuelStatus() != null) car.setFuelLevel(request.getFuelStatus());
        int extraMiles = request.getExtraMiles() != null ? request.getExtraMiles() : 0;
        car.setOdometer((car.getOdometer() != null ? car.getOdometer() : 0) + extraMiles);
//        carRepository.save(car);
//
//        return toDto(invoice);
        carRepository.save(car);

        /* -------- Send Invoice Email -------- */
        try {

        	byte[] pdf = invoicePdfService.generateInvoice(invoice.getInvoiceId());

        	emailService.sendBookingInvoice(
        	        customer.getEmail(),
        	        customer.getFullName(),
        	        header,
        	        invoice,
        	        pdf
        	);

        } catch (Exception e) {
            e.printStackTrace();
        }
        /* ------------------------------------ */

        return toDto(invoice);
    }

    @Override
    public List<CarDTO> getAvailableCarsForHandover(String confirmationNo) {
        BookingHeader header = findHeader(confirmationNo);
        return carRepository.findByHubIdAndCarTypeIdAndStatus(
                        header.getPickupHubId(), header.getCarTypeId(), CarStatus.AVAILABLE)
                .stream()
                .map(car -> vehicleService.getCarById(car.getCarId()))
                .collect(Collectors.toList());
    }

    private BookingHeader findHeader(String confirmationNo) {
        return bookingHeaderRepository.findByConfirmationNoIgnoreCase(confirmationNo)
                .orElseThrow(() -> new ResourceNotFoundException("No booking found for that confirmation number"));
    }

    @Override
    public List<BookingResponseDTO> getAllBookings() {
        // Every booking, customer info and car (type + registration number)
        // already nested in — the staff booking page's whole reason to
        // exist. Reuses BookingService's own DTO builder (getBookingByConfirmation)
        // rather than duplicating that mapping here, same "one source of
        // truth per entity type" reasoning BookingServiceImpl itself follows.
        return bookingHeaderRepository.findAll().stream()
                .sorted(Comparator.comparing(BookingHeader::getBookingDate).reversed())
                .map(h -> bookingService.getBookingByConfirmation(h.getConfirmationNo()))
                .collect(Collectors.toList());
    }

    @Override
    public List<CarTypeAvailabilityDTO> getDashboard(Long hubId) {
        // The custom aggregate query (CarRepository.countByCarType) does the
        // actual counting in one grouped query rather than pulling every
        // Car row into memory - this is the dashboard "X available / Y
        // handed over / Z in maintenance per car type" screen reads from.
        List<CarRepository.CarTypeCountProjection> counts = carRepository.countByCarType(
                hubId, CarStatus.AVAILABLE, CarStatus.BOOKED, CarStatus.UNDER_MAINTENANCE);
        return counts.stream()
                .map(p -> new CarTypeAvailabilityDTO(
                        p.getCarTypeId(),
                        carTypeRepository.findById(p.getCarTypeId()).map(CarType::getCarTypeName).orElse("Unknown"),
                        p.getTotalCars(), p.getAvailableCars(), p.getBookedCars(), p.getMaintenanceCars()))
                .collect(Collectors.toList());
    }

    private int daysBetween(LocalDateTime pickup, LocalDateTime returnDt) {
        long hours = Duration.between(pickup, returnDt).toHours();
        long fullDays = (long) Math.ceil(hours / 24.0);
        return (int) Math.max(1, fullDays);
    }

    private InvoiceResponseDTO toDto(InvoiceHeader inv) {
        InvoiceResponseDTO dto = new InvoiceResponseDTO();
        dto.setInvoiceId(inv.getInvoiceId());
        dto.setInvoiceDate(inv.getInvoiceDate());
        dto.setBookingId(inv.getBookingId());
        dto.setCustomerName(inv.getCustomerName());
        dto.setPhone(inv.getPhone());
        dto.setEmail(inv.getEmail());
        dto.setVehicleNumber(inv.getVehicleNumber());
        dto.setPickupHub(inv.getPickupHub());
        dto.setDropHub(inv.getDropHub());
        dto.setHandoverDatetime(inv.getHandoverDatetime());
        dto.setReturnDatetime(inv.getReturnDatetime());
        dto.setHandoverFuelLevel(inv.getHandoverFuelLevel());
        dto.setReturnFuelLevel(inv.getReturnFuelLevel());
        dto.setFuelCharge(inv.getFuelCharge());
        dto.setRentalAmount(inv.getRentalAmount());
        dto.setAddonAmount(inv.getAddonAmount());
        dto.setTotalAmount(inv.getTotalAmount());
        dto.setPaymentType(inv.getPaymentType() != null ? inv.getPaymentType().name() : null);
        dto.setPaymentReference(inv.getPaymentReference());
        dto.setPaymentStatus(inv.getPaymentStatus() != null ? inv.getPaymentStatus().name() : null);
        dto.setExtraMiles(inv.getExtraMiles());
        dto.setExtraChargeAmount(inv.getExtraChargeAmount());
        dto.setDamageNotes(inv.getDamageNotes());
        dto.setDays(inv.getDays());
        return dto;
    }
}
