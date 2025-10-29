package com.urlshortner.writeservice.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.urlshortner.writeservice.dto.response.ShortenUrlResponse;
import com.urlshortner.writeservice.model.ShortURL;
import com.urlshortner.writeservice.repository.ShortURLRepository;

@Service
public class ShortenerService {

    private final ShortURLRepository shortURLRepository;
    private static final String BASE62_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Autowired
    public ShortenerService(ShortURLRepository shortURLRepository) {
        this.shortURLRepository = shortURLRepository;
    }

    public ShortenUrlResponse shortenUrl(String longUrl, String userId, String customAlias, LocalDateTime expirationDate) {
        // Check if URL already exists
        Optional<ShortURL> existing = shortURLRepository.findByLongUrl(longUrl);
        if (existing.isPresent()) {
            return new ShortenUrlResponse("This URL has already been shortened", existing.get().getShortUrl());
        }
        
        String shortUrl;
        
        if (customAlias != null && !customAlias.trim().isEmpty()) {
            // Check if custom alias already exists
            Optional<ShortURL> existingAlias = shortURLRepository.findById(customAlias);
            if (existingAlias.isPresent()) {
                return new ShortenUrlResponse("This custom name is already taken", null);
            }
            shortUrl = customAlias;
        } else {
            // Generate new short URL using UUID
            shortUrl = generateShortUrl();
        }
        
        ShortURL entity = new ShortURL();
        entity.setShortUrl(shortUrl);
        entity.setLongUrl(longUrl);
        entity.setUserId(userId);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setExpirationDate(expirationDate);
        
        shortURLRepository.save(entity);
        return new ShortenUrlResponse("Short URL created successfully", shortUrl);
    }
    
    private String generateShortUrl() {
        UUID uuid = UUID.randomUUID();
        long mostSigBits = uuid.getMostSignificantBits();
        return encodeBase62(Math.abs(mostSigBits)).substring(0, 6);
    }
    
    private String encodeBase62(long number) {
        if (number == 0) return "aaaaaa";
        
        StringBuilder result = new StringBuilder();
        while (number > 0 && result.length() < 6) {
            result.append(BASE62_CHARS.charAt((int)(number % 62)));
            number /= 62;
        }
        
        // Pad with 'a' if needed to ensure 6 characters
        while (result.length() < 6) {
            result.append('a');
        }
        
        return result.reverse().toString();
    }
}
