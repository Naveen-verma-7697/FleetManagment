package impl;

import com.fleman.dto.CarDTO;
import com.fleman.entity.Car;
import com.fleman.entity.Car.CarStatus;
import com.fleman.repository.BookingHeaderRepository;
import com.fleman.repository.CarRepository;
import com.fleman.repository.CarTypeRepository;
import com.fleman.service.impl.VehicleServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehicleServiceImplTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private CarTypeRepository carTypeRepository;

    @Mock
    private BookingHeaderRepository bookingHeaderRepository;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    @Test
    void testGetCarByIdSuccess() {

        Car car = new Car();
        car.setCarId(1L);
        car.setVehicleNumber("MH12AB1234");
        car.setBrand("Hyundai");
        car.setModel("Creta");
        car.setHubId(1L);
        car.setCarTypeId(1L);
        car.setStatus(CarStatus.AVAILABLE);
        car.setAvailable(true);

        when(carRepository.findById(1L))
                .thenReturn(Optional.of(car));

        CarDTO result = vehicleService.getCarById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getCarId());
        assertEquals("MH12AB1234", result.getVehicleNumber());
        assertEquals("Hyundai", result.getBrand());
        assertEquals("Creta", result.getModel());
        assertTrue(result.isBookableNow());
    }
}