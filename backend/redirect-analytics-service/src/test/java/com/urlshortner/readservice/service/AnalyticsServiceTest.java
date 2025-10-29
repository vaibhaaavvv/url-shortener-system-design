package com.urlshortner.readservice.service;

import com.urlshortner.readservice.repository.ClickEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private ClickEventRepository clickEventRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    @Test
    void testDateRangeFiltering() {
        String shortUrl = "test123";
        String startDate = "2024-12-01";
        String endDate = "2024-12-31";
        
        LocalDateTime startDateTime = LocalDateTime.of(2024, 12, 1, 0, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2025, 1, 1, 0, 0, 0);

        List<Object[]> mockResults = Arrays.asList(
            new Object[]{java.sql.Date.valueOf("2024-12-15"), 5L},
            new Object[]{java.sql.Date.valueOf("2024-12-20"), 3L}
        );

        when(clickEventRepository.countClicksByShortUrlAndDateRange(shortUrl, startDateTime, endDateTime))
            .thenReturn(mockResults);

        Map<String, Long> result = analyticsService.getDailyClicks(shortUrl, startDate, endDate);

        assertEquals(2, result.size());
        assertEquals(5L, result.get("2024-12-15"));
        assertEquals(3L, result.get("2024-12-20"));
        verify(clickEventRepository).countClicksByShortUrlAndDateRange(shortUrl, startDateTime, endDateTime);
    }

    @Test
    void testDailyGroupingAccuracy() {
        String shortUrl = "test123";
        String startDate = "2024-12-01";
        String endDate = "2024-12-02";
        
        LocalDateTime startDateTime = LocalDateTime.of(2024, 12, 1, 0, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2024, 12, 3, 0, 0, 0);

        List<Object[]> mockResults = Arrays.asList(
            new Object[]{java.sql.Date.valueOf("2024-12-01"), 10L},
            new Object[]{java.sql.Date.valueOf("2024-12-02"), 15L}
        );

        when(clickEventRepository.countClicksByShortUrlAndDateRange(shortUrl, startDateTime, endDateTime))
            .thenReturn(mockResults);

        Map<String, Long> result = analyticsService.getDailyClicks(shortUrl, startDate, endDate);

        assertEquals(2, result.size());
        assertEquals(10L, result.get("2024-12-01"));
        assertEquals(15L, result.get("2024-12-02"));
    }
}