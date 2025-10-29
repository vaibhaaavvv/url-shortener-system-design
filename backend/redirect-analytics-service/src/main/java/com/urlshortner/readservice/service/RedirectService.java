package com.urlshortner.readservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.urlshortner.readservice.model.ShortURL;
import com.urlshortner.readservice.repository.ShortURLRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class RedirectService {

    @Autowired
    private ShortURLRepository shortURLRepository;
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public String getLongUrl(String shortUrl, String userAgent) {
        String urlCacheKey = "url:" + shortUrl;
        String cachedUrl = null;
        
        //Try URL mapping cache first
        try {
            cachedUrl = redisTemplate.opsForValue().get(urlCacheKey);
            if (cachedUrl != null) {
                recordClickEvent(shortUrl, userAgent);
                return cachedUrl;
            }
        } catch (Exception e) {
            //Redis unavailable, fallback to database
        }
        
        //Fallback to database
        Optional<ShortURL> entity = shortURLRepository.findById(shortUrl);
        if (entity.isPresent()) {
            ShortURL shortURL = entity.get();
            String longUrl = shortURL.getLongUrl();
            
            // Try to cache in URL mapping cache
            try {
                redisTemplate.opsForValue().set(urlCacheKey, longUrl);
            } catch (Exception e) {
                //Redis unavailable, continue without caching
            }
            
            recordClickEvent(shortUrl, userAgent);
            
            return longUrl;
        }
        return null;
    }
    
    private void recordClickEvent(String shortUrl, String userAgent) {
        try {
            Map<String, String> clickData = new HashMap<>();
            clickData.put("shortUrl", shortUrl);
            clickData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            clickData.put("userAgent", userAgent != null ? userAgent : "unknown");
            
            redisTemplate.opsForStream().add("clicks-stream", clickData);
        } catch (Exception e) {
            //Redis unavailable, skip click event recording
        }
    }
}