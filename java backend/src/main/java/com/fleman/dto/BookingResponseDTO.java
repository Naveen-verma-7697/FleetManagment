package com.fleman.dto;

import java.time.LocalDateTime;
import java.util.List;

// The shape every booking-related endpoint returns — this is a direct
// translation of the frontend mock's enrich(header) function: the raw
// booking fields, plus car/carType/customer/pickupHub/dropHub/addonLines
// all nested in, so the UI never has to make four follow-up requests.
public class BookingResponseDTO {
    private Long bookingId;
    private String confirmationNo;
    private LocalDateTime bookingDate;
    private Long customerId;
    private Long carId;
    private Long pickupHubId;
    private Long dropHubId;
    private LocalDateTime pickupDatetime;
    private LocalDateTime returnDatetime;
    private LocalDateTime actualHandoverDatetime;
    private LocalDateTime actualReturnDatetime;
    private Integer handoverFuelLevel;
    private String bookingStatus;
    private Double estimatedAmount;
    private String remarks;

    private CarDTO car;
    private CarTypeDTO carType;
    private CustomerDTO customer;
    private HubDTO pickupHub;
    private HubDTO dropHub;
    private List<BookingDetailDTO> addonLines;

    public BookingResponseDTO() {}

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
    public String getConfirmationNo() { return confirmationNo; }
    public void setConfirmationNo(String confirmationNo) { this.confirmationNo = confirmationNo; }
    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
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
    public String getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(String bookingStatus) { this.bookingStatus = bookingStatus; }
    public Double getEstimatedAmount() { return estimatedAmount; }
    public void setEstimatedAmount(Double estimatedAmount) { this.estimatedAmount = estimatedAmount; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public CarDTO getCar() { return car; }
    public void setCar(CarDTO car) { this.car = car; }
    public CarTypeDTO getCarType() { return carType; }
    public void setCarType(CarTypeDTO carType) { this.carType = carType; }
    public CustomerDTO getCustomer() { return customer; }
    public void setCustomer(CustomerDTO customer) { this.customer = customer; }
    public HubDTO getPickupHub() { return pickupHub; }
    public void setPickupHub(HubDTO pickupHub) { this.pickupHub = pickupHub; }
    public HubDTO getDropHub() { return dropHub; }
    public void setDropHub(HubDTO dropHub) { this.dropHub = dropHub; }
    public List<BookingDetailDTO> getAddonLines() { return addonLines; }
    public void setAddonLines(List<BookingDetailDTO> addonLines) { this.addonLines = addonLines; }
}
