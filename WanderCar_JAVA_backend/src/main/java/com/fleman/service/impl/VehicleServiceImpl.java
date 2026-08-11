package com.fleman.service.impl;

import com.fleman.dto.CarDTO;
import com.fleman.dto.CarTypeDTO;
import com.fleman.entity.Car;
import com.fleman.entity.Car.CarStatus;
import com.fleman.entity.BookingHeader.BookingStatus;
import com.fleman.entity.CarType;
import com.fleman.exception.ResourceNotFoundException;
import com.fleman.repository.BookingHeaderRepository;
import com.fleman.repository.CarRepository;
import com.fleman.repository.CarTypeRepository;
import com.fleman.service.VehicleService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleServiceImpl implements VehicleService {

    private static final List<BookingStatus> ACTIVE_STATUSES = List.of(BookingStatus.CONFIRMED, BookingStatus.ONGOING);

    private final CarRepository carRepository;
    private final CarTypeRepository carTypeRepository;
    private final BookingHeaderRepository bookingHeaderRepository;

    public VehicleServiceImpl(CarRepository carRepository, CarTypeRepository carTypeRepository,
                               BookingHeaderRepository bookingHeaderRepository) {
        this.carRepository = carRepository;
        this.carTypeRepository = carTypeRepository;
        this.bookingHeaderRepository = bookingHeaderRepository;
    }

    @Override
    public List<CarTypeDTO> getCarTypes() {
        return carTypeRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<CarTypeDTO> getCarTypesForHub(Long hubId, LocalDateTime pickupDatetime, LocalDateTime returnDatetime) {
        if (hubId == null) return getCarTypes();
        // Category-level (not per-car) availability: capacity is the
        // physical fleet of that type at this hub minus anything under
        // maintenance; demand is how many CONFIRMED/ONGOING bookings
        // already reserve that type here for an overlapping date range.
        // Every type is always returned — a type with availableCount 0 is
        // shown disabled on the vehicle-selection page, not hidden, so the
        // customer can see it exists and why it's currently unbookable.
        return carTypeRepository.findAll().stream()
                .map(ct -> {
                    CarTypeDTO dto = toDto(ct);
                    long capacity = carRepository.countByHubIdAndCarTypeIdAndStatusNot(
                            hubId, ct.getCarTypeId(), CarStatus.UNDER_MAINTENANCE);
                    long overlapping = (pickupDatetime != null && returnDatetime != null)
                            ? bookingHeaderRepository.countOverlapping(
                                    hubId, ct.getCarTypeId(), pickupDatetime, returnDatetime, ACTIVE_STATUSES, null)
                            : 0;
                    dto.setAvailableCount((int) Math.max(0, capacity - overlapping));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<CarDTO> getAvailableCars(Long hubId, LocalDateTime pickupDatetime, LocalDateTime returnDatetime) {
        if (hubId == null) {
            return carRepository.findAll().stream().map(c -> toDto(c, true)).collect(Collectors.toList());
        }
        // Two different questions, deliberately not conflated: "which cars
        // exist at this hub" (every one of them, so a car mid-rental still
        // shows up - see bookableNow's javadoc on CarDTO) vs "which of them
        // are physically free right now" (plain status check - individual
        // cars are no longer reserved by date, only the category is; see
        // getCarTypesForHub for that capacity/overlap check).
        List<Car> allAtHub = carRepository.findByHubId(hubId);
        return allAtHub.stream()
                .map(c -> toDto(c, c.getStatus() == CarStatus.AVAILABLE))
                .collect(Collectors.toList());
    }

    @Override
    public CarDTO getCarById(Long carId) {
        return carRepository.findById(carId).map(c -> toDto(c, c.getStatus() == CarStatus.AVAILABLE))
                .orElseThrow(() -> new ResourceNotFoundException("Car not found: " + carId));
    }

    @Override
    public CarTypeDTO getCarTypeById(Long carTypeId) {
        return carTypeRepository.findById(carTypeId).map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Car type not found: " + carTypeId));
    }

    private CarDTO toDto(Car car, boolean bookableNow) {
        CarDTO dto = new CarDTO();
        dto.setCarId(car.getCarId());
        dto.setCarTypeId(car.getCarTypeId());
        dto.setHubId(car.getHubId());
        dto.setVehicleNumber(car.getVehicleNumber());
        dto.setBrand(car.getBrand());
        dto.setModel(car.getModel());
        dto.setManufactureYear(car.getManufactureYear());
        dto.setColor(car.getColor());
        dto.setFuelType(car.getFuelType() != null ? car.getFuelType().name() : null);
        dto.setSeatingCapacity(car.getSeatingCapacity());
        dto.setMileage(car.getMileage());
        dto.setOdometer(car.getOdometer());
        dto.setFuelLevel(car.getFuelLevel());
        dto.setAvailable(car.isAvailable());
        dto.setStatus(car.getStatus() != null ? car.getStatus().name() : null);
        dto.setBookableNow(bookableNow);
        // No relationship — an explicit second lookup by the plain
        // carTypeId column to build the nested CarTypeDTO the frontend expects.
        carTypeRepository.findById(car.getCarTypeId()).ifPresent(ct -> dto.setCarType(toDto(ct)));
        return dto;
    }

    private CarTypeDTO toDto(CarType ct) {
        CarTypeDTO dto = new CarTypeDTO();
        dto.setCarTypeId(ct.getCarTypeId());
        dto.setCarTypeName(ct.getCarTypeName());
        dto.setDailyRate(ct.getDailyRate());
        dto.setWeeklyRate(ct.getWeeklyRate());
        dto.setMonthlyRate(ct.getMonthlyRate());
        dto.setRateValidFrom(ct.getRateValidFrom());
        dto.setRateValidTo(ct.getRateValidTo());
        dto.setImagePath(ct.getImagePath());
        return dto;
    }
}
