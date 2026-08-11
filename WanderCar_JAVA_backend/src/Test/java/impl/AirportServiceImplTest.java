package impl;

import com.fleman.dto.AirportDTO;
import com.fleman.dto.AirportRequest;
import com.fleman.entity.Airport;
import com.fleman.exception.ApiException;
import com.fleman.exception.ResourceNotFoundException;
import com.fleman.repository.AirportRepository;
import com.fleman.repository.CityRepository;
import com.fleman.repository.HubRepository;
import com.fleman.repository.StateRepository;
import com.fleman.service.impl.AirportServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AirportServiceImplTest {

    @Mock
    private AirportRepository airportRepository;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private StateRepository stateRepository;

    @Mock
    private HubRepository hubRepository;

    @InjectMocks
    private AirportServiceImpl airportService;

    @Test
    void testGetAllAirports() {

        Airport airport = new Airport();
        airport.setAirportId(1L);
        airport.setAirportCode("BOM");
        airport.setAirportName("Mumbai Airport");
        airport.setCityId(1L);
        airport.setStateId(1L);
        airport.setHubId(1L);

        when(airportRepository.findAll()).thenReturn(Arrays.asList(airport));

        var result = airportService.getAllAirports();

        assertEquals(1, result.size());
        assertEquals("BOM", result.get(0).getAirportCode());

        verify(airportRepository).findAll();
    }

    @Test
    void testCreateAirport() {

        AirportRequest request = new AirportRequest();
        request.setAirportCode("DEL");
        request.setAirportName("Delhi Airport");
        request.setCityId(1L);
        request.setStateId(1L);
        request.setHubId(1L);

        when(airportRepository.existsByAirportCodeIgnoreCase("DEL"))
                .thenReturn(false);

        when(cityRepository.existsById(1L)).thenReturn(true);
        when(stateRepository.existsById(1L)).thenReturn(true);
        when(hubRepository.existsById(1L)).thenReturn(true);

        Airport airport = new Airport();
        airport.setAirportId(1L);
        airport.setAirportCode("DEL");
        airport.setAirportName("Delhi Airport");
        airport.setCityId(1L);
        airport.setStateId(1L);
        airport.setHubId(1L);

        when(airportRepository.save(any(Airport.class)))
                .thenReturn(airport);

        AirportDTO dto = airportService.createAirport(request);

        assertNotNull(dto);
        assertEquals("DEL", dto.getAirportCode());

        verify(airportRepository).save(any(Airport.class));
    }

    @Test
    void testCreateAirportDuplicateCode() {

        AirportRequest request = new AirportRequest();
        request.setAirportCode("BOM");

        when(airportRepository.existsByAirportCodeIgnoreCase("BOM"))
                .thenReturn(true);

        assertThrows(ApiException.class,
                () -> airportService.createAirport(request));

        verify(airportRepository, never()).save(any());
    }

    @Test
    void testGetAirportById() {

        Airport airport = new Airport();
        airport.setAirportId(1L);
        airport.setAirportCode("BOM");
        airport.setAirportName("Mumbai");

        when(airportRepository.findById(1L))
                .thenReturn(Optional.of(airport));

        AirportDTO dto = airportService.getAirportById(1L);

        assertEquals("BOM", dto.getAirportCode());
    }

    @Test
    void testGetAirportByIdNotFound() {

        when(airportRepository.findById(100L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> airportService.getAirportById(100L));
    }

    @Test
    void testDeleteAirport() {

        when(airportRepository.existsById(1L))
                .thenReturn(true);

        airportService.deleteAirport(1L);

        verify(airportRepository).deleteById(1L);
    }

    @Test
    void testDeleteAirportNotFound() {

        when(airportRepository.existsById(5L))
                .thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> airportService.deleteAirport(5L));

        verify(airportRepository, never()).deleteById(anyLong());
    }
}