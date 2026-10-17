package com.carpinteria.api;

import com.carpinteria.api.dto.BulkPriceUpdateRequest;
import com.carpinteria.api.dto.BulkPriceUpdateResult;
import com.carpinteria.api.model.AluminiumProfile;
import com.carpinteria.api.repository.AluminiumProfileRepository;
import com.carpinteria.api.service.AluminiumProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AluminiumProfileServiceTest {

    @Mock
    private AluminiumProfileRepository repository;

    @InjectMocks
    private AluminiumProfileService service;

    private AluminiumProfile profile1;
    private AluminiumProfile profile2;

    @BeforeEach
    void setUp() {
        profile1 = new AluminiumProfile(
                "AL-TEST-01",
                "Marco Europeo",
                "Serie 40",
                "Blanco",
                new BigDecimal("6.00"),
                new BigDecimal("1.00"),
                new BigDecimal("10.00"),
                new BigDecimal("60.00"),
                10
        );

        profile2 = new AluminiumProfile(
                "AL-TEST-02",
                "Hoja Europea",
                "Serie 40",
                "Blanco",
                new BigDecimal("6.00"),
                new BigDecimal("0.90"),
                new BigDecimal("20.00"),
                new BigDecimal("120.00"),
                5
        );
    }

    @Test
    void testBulkUpdatePricesPercentage() {
        when(repository.findBySeriesIgnoreCase("Serie 40")).thenReturn(List.of(profile1, profile2));
        when(repository.save(any(AluminiumProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Increase by 10%
        BulkPriceUpdateRequest request = new BulkPriceUpdateRequest();
        request.setSeries("Serie 40");
        request.setPercentage(new BigDecimal("10.0"));
        request.setTargetPriceField("ALL");

        BulkPriceUpdateResult result = service.bulkUpdatePrices(request);

        assertNotNull(result);
        assertEquals(2, result.getUpdatedCount());
        // 10.00 + 10% = 11.00
        assertEquals(new BigDecimal("11.00"), profile1.getPricePerMeter());
        // 60.00 + 10% = 66.00
        assertEquals(new BigDecimal("66.00"), profile1.getPricePerBar());
        // 20.00 + 10% = 22.00
        assertEquals(new BigDecimal("22.00"), profile2.getPricePerMeter());
    }
}
