package com.fleman.service;

import com.fleman.dto.CarDTO;
import com.fleman.dto.CarTypeDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface VehicleService {
    List<CarTypeDTO> getCarTypes();
    List<CarTypeDTO> getCarTypesForHub(Long hubId, LocalDateTime pickupDatetime, LocalDateTime returnDatetime);
    List<CarDTO> getAvailableCars(Long hubId, LocalDateTime pickupDatetime, LocalDateTime returnDatetime);
    CarDTO getCarById(Long carId);
    // Looks up a single category by id, no hub/date scoping — used by
    // BookingServiceImpl to render a booking's reserved carType even
    // before any specific car has been assigned to it.
    CarTypeDTO getCarTypeById(Long carTypeId);
}
