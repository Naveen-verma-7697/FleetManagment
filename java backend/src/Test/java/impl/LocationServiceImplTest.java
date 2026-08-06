package com.fleman.service.impl;

import com.fleman.dto.HubDTO;
import com.fleman.entity.Hub;
import com.fleman.repository.AirportRepository;
import com.fleman.repository.CityRepository;
import com.fleman.repository.HubRepository;
import com.fleman.repository.StateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationServiceImplTest {

    @Mock
    private StateRepository stateRepository;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private HubRepository hubRepository;

    @Mock
    private AirportRepository airportRepository;

    @InjectMocks
    private LocationServiceImpl locationService;

    @Test
    void testGetHubByIdSuccess() {

        Hub hub = new Hub();
        hub.setHubId(1L);
        hub.setHubName("Mumbai Hub");
        hub.setAddress("Andheri");
        hub.setCityId(10L);
        hub.setStateId(20L);
        hub.setPincode("400001");
        hub.setContactNo("9876543210");
        hub.setEmail("hub@gmail.com");

        when(hubRepository.findById(1L))
                .thenReturn(Optional.of(hub));

        HubDTO result = locationService.getHubById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getHubId());
        assertEquals("Mumbai Hub", result.getHubName());
        assertEquals("Andheri", result.getAddress());

        verify(hubRepository).findById(1L);
    }
}
