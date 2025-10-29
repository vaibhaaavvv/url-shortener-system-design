package com.urlshortner.readservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.urlshortner.readservice.service.AnalyticsService;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/analytics/click-counts")
    public ResponseEntity<Map<String, Long>> getClickCounts(@RequestParam List<String> shortUrls) {
        Map<String, Long> clickCounts = analyticsService.getClickCounts(shortUrls);
        return ResponseEntity.ok(clickCounts);
    }

    @GetMapping("/analytics/daily-clicks")
    public ResponseEntity<Map<String, Long>> getDailyClicks(
            @RequestParam String shortUrl,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        Map<String, Long> dailyClicks = analyticsService.getDailyClicks(shortUrl, startDate, endDate);
        return ResponseEntity.ok(dailyClicks);
    }
}