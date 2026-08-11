package impl;

import com.fleman.dto.HandoverRequest;
import com.fleman.entity.BookingHeader;
import com.fleman.entity.Car;
import com.fleman.entity.BookingHeader.BookingStatus;
import com.fleman.entity.Car.CarStatus;
import com.fleman.repository.*;
import com.fleman.service.BookingService;
import com.fleman.service.VehicleService;
import com.fleman.service.impl.StaffServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StaffServiceImplTest {

    @Mock
    BookingHeaderRepository bookingHeaderRepository;

    @Mock
    BookingDetailRepository bookingDetailRepository;

    @Mock
    CarRepository carRepository;

    @Mock
    CarTypeRepository carTypeRepository;

    @Mock
    CustomerRepository customerRepository;

    @Mock
    AddonRepository addonRepository;

    @Mock
    InvoiceHeaderRepository invoiceHeaderRepository;

    @Mock
    InvoiceDetailRepository invoiceDetailRepository;

    @Mock
    BookingService bookingService;

    @Mock
    VehicleService vehicleService;

    @InjectMocks
    StaffServiceImpl staffService;

    @Test
    void testHandoverVehicleSuccess() {

        BookingHeader booking = new BookingHeader();
        booking.setBookingId(1L);
        booking.setConfirmationNo("WDR-1001");
        booking.setPickupHubId(1L);
        booking.setCarTypeId(1L);
        booking.setBookingStatus(BookingStatus.CONFIRMED);

        Car car = new Car();
        car.setCarId(10L);
        car.setHubId(1L);
        car.setCarTypeId(1L);
        car.setStatus(CarStatus.AVAILABLE);

        HandoverRequest request = new HandoverRequest();
        request.setCarId(10L);

        when(bookingHeaderRepository.findByConfirmationNoIgnoreCase("WDR-1001"))
                .thenReturn(Optional.of(booking));

        when(carRepository.findById(10L))
                .thenReturn(Optional.of(car));

        when(carRepository.save(any(Car.class)))
                .thenReturn(car);

        when(bookingHeaderRepository.save(any(BookingHeader.class)))
                .thenReturn(booking);

        when(bookingService.getBookingByConfirmation("WDR-1001"))
                .thenReturn(new com.fleman.dto.BookingResponseDTO());

        assertNotNull(staffService.handoverVehicle("WDR-1001", request));

        verify(carRepository).save(any(Car.class));
        verify(bookingHeaderRepository).save(any(BookingHeader.class));
    }
}