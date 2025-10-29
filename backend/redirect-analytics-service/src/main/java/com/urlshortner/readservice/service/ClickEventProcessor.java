package com.urlshortner.readservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.urlshortner.readservice.model.ClickEvent;
import com.urlshortner.readservice.repository.ClickEventRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClickEventProcessor {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    @Autowired
    private ClickEventRepository clickEventRepository;
    
    private static final String STREAM_KEY = "clicks-stream";

    @Scheduled(fixedRate = 10000) // Process every 10 seconds
    public void processClickEvents() {
        try {
            // Read all messages from stream
            List<MapRecord<String, Object, Object>> records = redisTemplate.opsForStream()
                .read(StreamOffset.fromStart(STREAM_KEY));
            
            if (!records.isEmpty()) {
                List<ClickEvent> clickEvents = new ArrayList<>();
                
                // Process each record
                for (MapRecord<String, Object, Object> record : records) {
                    ClickEvent clickEvent = createClickEvent(record.getValue());
                    if (clickEvent != null) {
                        clickEvents.add(clickEvent);
                    }
                }
                
                // Batch save to database
                if (!clickEvents.isEmpty()) {
                    clickEventRepository.saveAll(clickEvents);
                    
                    // Clear the stream after successful save
                    redisTemplate.delete(STREAM_KEY);
                }
            }
        } catch (Exception e) {
            System.err.println("Error processing click events: " + e.getMessage());
        }
    }
    
    private ClickEvent createClickEvent(java.util.Map<Object, Object> record) {
        try {
            String shortUrl = (String) record.get("shortUrl");
            String timestampStr = (String) record.get("timestamp");
            String userAgent = (String) record.get("userAgent");
            
            LocalDateTime timestamp = LocalDateTime.parse(timestampStr, 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            ClickEvent clickEvent = new ClickEvent();
            clickEvent.setShortUrl(shortUrl);
            clickEvent.setTimestamp(timestamp);
            clickEvent.setUserAgent(userAgent);
            
            return clickEvent;
        } catch (Exception e) {
            System.err.println("Error creating click event: " + e.getMessage());
            return null;
        }
    }
}