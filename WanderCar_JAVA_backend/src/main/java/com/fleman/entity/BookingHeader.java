package com.fleman.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// customerId, carId, carTypeId, pickupHubId, dropHubId are all plain
// logical-reference columns — no @ManyToOne, no database foreign keys
// anywhere in this project.
@Entity
@Table(name = "booking_headers")
public class BookingHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @Column(nullable = false, unique = true)
    private String confirmationNo;

    @Column(nullable = false)
    private LocalDateTime bookingDate;

    @Column(nullable = false)
    private Long customerId;

    // The category reserved at booking time — always set. The actual
    // vehicle isn't picked until staff hand-over (see carId below), so this
    // is what createBooking/modifyBooking check availability against.
    @Column(nullable = false)
    private Long carTypeId;

    // Null until a staff member hands over a specific car at pickup — see
    // StaffServiceImpl.handoverVehicle. Before that, the booking only
    // reserves a carTypeId, not any particular vehicle.
    private Long carId;

    @Column(nullable = false)
    private Long pickupHubId;

    @Column(nullable = false)
    private Long dropHubId;

    @Column(nullable = false)
    private LocalDateTime pickupDatetime;

    @Column(nullable = false)
    private LocalDateTime returnDatetime;

    // When staff actually handed over / took back the vehicle — set by
    // StaffServiceImpl.handoverVehicle and .processReturn respectively.
    // These, not the planned pickupDatetime/returnDatetime above, are what
    // the return invoice bills against.
    private LocalDateTime actualHandoverDatetime;
    private LocalDateTime actualReturnDatetime;

    // The car's fuel level (25/50/75/100) at the moment of hand-over, as
    // staff recorded it — captured here because Car.fuelLevel itself gets
    // overwritten again at return, so by then there'd be nothing left to
    // compare the return level against. Used to bill a fuel shortfall.
    private Integer handoverFuelLevel;

    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

    private Double estimatedAmount;

    @Column(length = 255)
    private String remarks;

    public BookingHeader() {}

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
    public String getConfirmationNo() { return confirmationNo; }
    public void setConfirmationNo(String confirmationNo) { this.confirmationNo = confirmationNo; }
    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public Long getCarTypeId() { return carTypeId; }
    public void setCarTypeId(Long carTypeId) { this.carTypeId = carTypeId; }
    public Long getCarId() { return carId; }
    public void setCarId(Long carId) { this.carId = carId; }
    public Long getPickupHubId() { return pickupHubId; }
    public void setPickupHubId(Long pickupHubId) { this.pickupHubId = pickupHubId; }
    public Long getDropHubId() { return dropHubId; }
    public void setDropHubId(Long dropHubId) { this.dropHubId = dropHubId; }
    public LocalDateTime getPickupDatetime() { return pickupDatetime; }
    public void setPickupDatetime(LocalDateTime pickupDatetime) { this.pickupDatetime = pickupDatetime; }
    public LocalDateTime getReturnDatetime() { return returnDatetime; }
    public void setReturnDatetime(LocalDateTime returnDatetime) { this.returnDatetime = returnDatetime; }
    public LocalDateTime getActualHandoverDatetime() { return actualHandoverDatetime; }
    public void setActualHandoverDatetime(LocalDateTime actualHandoverDatetime) { this.actualHandoverDatetime = actualHandoverDatetime; }
    public LocalDateTime getActualReturnDatetime() { return actualReturnDatetime; }
    public void setActualReturnDatetime(LocalDateTime actualReturnDatetime) { this.actualReturnDatetime = actualReturnDatetime; }
    public Integer getHandoverFuelLevel() { return handoverFuelLevel; }
    public void setHandoverFuelLevel(Integer handoverFuelLevel) { this.handoverFuelLevel = handoverFuelLevel; }
    public BookingStatus getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(BookingStatus bookingStatus) { this.bookingStatus = bookingStatus; }
    public Double getEstimatedAmount() { return estimatedAmount; }
    public void setEstimatedAmount(Double estimatedAmount) { this.estimatedAmount = estimatedAmount; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public enum BookingStatus { PENDING, CONFIRMED, ONGOING, COMPLETED, CANCELLED }
}
