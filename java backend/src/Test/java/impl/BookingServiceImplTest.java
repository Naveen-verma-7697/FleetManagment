package com.fleman.service.impl;

import com.fleman.dto.CreateBookingRequest;
import com.fleman.dto.BookingResponseDTO;
import com.fleman.entity.BookingHeader;
import com.fleman.entity.CarType;
import com.fleman.repository.*;
import com.fleman.service.AddonService;
import com.fleman.service.CustomerService;
import com.fleman.service.LocationService;
import com.fleman.service.VehicleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

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
    HubRepository hubRepository;

    @Mock
    AddonRepository addonRepository;

    @Mock
    VehicleService vehicleService;

    @Mock
    CustomerService customerService;

    @Mock
    LocationService locationService;

    @Mock
    AddonService addonService;

    @InjectMocks
    BookingServiceImpl bookingService;

    @Test
    void testCreateBookingSuccess() {

        CreateBookingRequest request = new CreateBookingRequest();

        request.setCustomerId(1L);
        request.setCarTypeId(1L);
        request.setPickupHubId(1L);
        request.setDropHubId(1L);
        request.setPickupDatetime(LocalDateTime.now().plusDays(1));
        request.setReturnDatetime(LocalDateTime.now().plusDays(3));

        CarType carType = new CarType();
        carType.setCarTypeId(1L);
        carType.setDailyRate(2000.0);

        when(carTypeRepository.findById(1L))
                .thenReturn(Optional.of(carType));

        when(customerRepository.existsById(1L))
                .thenReturn(true);

        when(hubRepository.existsById(1L))
                .thenReturn(true);

        when(carRepository.countByHubIdAndCarTypeIdAndStatusNot(anyLong(), anyLong(), any()))
                .thenReturn(5L);

        when(bookingHeaderRepository.countOverlapping(anyLong(), anyLong(), any(), any(), any(), any()))
                .thenReturn(0L);

        when(bookingHeaderRepository.save(any(BookingHeader.class)))
                .thenAnswer(i -> {
                    BookingHeader b = i.getArgument(0);
                    b.setBookingId(10L);
                    return b;
                });

        BookingResponseDTO response = bookingService.createBooking(request);

        assertNotNull(response);

        verify(bookingHeaderRepository, atLeastOnce()).save(any());

    }
}