package impl;

import com.fleman.dto.AddonDTO;
import com.fleman.entity.Addon;
import com.fleman.repository.AddonRepository;
import com.fleman.service.impl.AddonServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddonServiceImplTest {

    @Mock
    private AddonRepository addonRepository;

    @InjectMocks
    private AddonServiceImpl addonService;

    @Test
    void testGetAddons() {

        // Arrange
        Addon addon1 = new Addon();
        addon1.setAddonId(1L);
        addon1.setAddonName("GPS");
        addon1.setDailyRate(200.0);
        addon1.setRateValidFrom(LocalDate.now());
        addon1.setRateValidTo(LocalDate.now().plusDays(30));
        addon1.setDescription("GPS Navigation");

        Addon addon2 = new Addon();
        addon2.setAddonId(2L);
        addon2.setAddonName("Baby Seat");
        addon2.setDailyRate(150.0);
        addon2.setRateValidFrom(LocalDate.now());
        addon2.setRateValidTo(LocalDate.now().plusDays(30));
        addon2.setDescription("Child Seat");

        when(addonRepository.findAll())
                .thenReturn(Arrays.asList(addon1, addon2));

        // Act
        List<AddonDTO> result = addonService.getAddons();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("GPS", result.get(0).getAddonName());
        assertEquals(200.0, result.get(0).getDailyRate());

        assertEquals("Baby Seat", result.get(1).getAddonName());
        assertEquals(150.0, result.get(1).getDailyRate());

        verify(addonRepository, times(1)).findAll();
    }
}