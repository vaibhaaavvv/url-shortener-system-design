package com.urlshortner.readservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.urlshortner.readservice.repository.ClickEventRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class AnalyticsService {
    
    @Autowired
    private ClickEventRepository clickEventRepository;
    
    public Map<String, Long> getClickCounts(List<String> shortUrls) {
        Map<String, Long> clickCounts = clickEventRepository.countClicksByShortUrls(shortUrls)
            .stream()
            .collect(Collectors.toMap(
                result -> (String) result[0],
                result -> (Long) result[1]
            ));
        
        return shortUrls.stream()
            .collect(Collectors.toMap(
                url -> url,
                url -> clickCounts.getOrDefault(url, 0L)
            ));
    }
    
    public Map<String, Long> getDailyClicks(String shortUrl, String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        return clickEventRepository.countClicksByShortUrlAndDateRange(shortUrl, start.atStartOfDay(), end.plusDays(1).atStartOfDay())
            .stream()
            .collect(Collectors.toMap(
                result -> ((java.sql.Date) result[0]).toString(),
                result -> (Long) result[1]
            ));
    }
}
