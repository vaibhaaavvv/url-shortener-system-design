package com.urlshortner.writeservice.service;

import com.urlshortner.writeservice.dto.response.ShortenUrlResponse;
import com.urlshortner.writeservice.model.ShortURL;
import com.urlshortner.writeservice.repository.ShortURLRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShortenerServiceTest {

    @Mock
    private ShortURLRepository shortURLRepository;

    @InjectMocks
    private ShortenerService shortenerService;

    @Test
    void testDuplicateUrlHandling() {
        String longUrl = "https://www.example.com";
        String userId = "test-user-id";
        String existingShortUrl = "test123";

        ShortURL existingEntity = new ShortURL();
        existingEntity.setShortUrl(existingShortUrl);
        existingEntity.setLongUrl(longUrl);
        existingEntity.setUserId(userId);
        existingEntity.setCreatedAt(LocalDateTime.now());

        when(shortURLRepository.findByLongUrl(longUrl)).thenReturn(Optional.of(existingEntity));

        ShortenUrlResponse response = shortenerService.shortenUrl(longUrl, userId, null, null);

        assertEquals(existingShortUrl, response.getShortUrl());
        assertEquals("This URL has already been shortened", response.getMessage());
        verify(shortURLRepository, never()).save(any());
    }

    @Test
    void testShortUrlUniqueness() {
        String longUrl1 = "https://www.example1.com";
        String longUrl2 = "https://www.example2.com";
        String userId = "test-user-id";

        when(shortURLRepository.findByLongUrl(anyString())).thenReturn(Optional.empty());
        when(shortURLRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Set<String> generatedUrls = new HashSet<>();
        
        for (int i = 0; i < 10; i++) {
            ShortenUrlResponse response1 = shortenerService.shortenUrl(longUrl1 + i, userId, null, null);
            ShortenUrlResponse response2 = shortenerService.shortenUrl(longUrl2 + i, userId, null, null);
            
            assertNotNull(response1.getShortUrl());
            assertNotNull(response2.getShortUrl());
            assertEquals(6, response1.getShortUrl().length());
            assertEquals(6, response2.getShortUrl().length());
            
            assertTrue(generatedUrls.add(response1.getShortUrl()), "Duplicate short URL generated");
            assertTrue(generatedUrls.add(response2.getShortUrl()), "Duplicate short URL generated");
        }
    }

    @Test
    void testDatabaseUnavailableErrorHandling() {
        String longUrl = "https://www.example.com";
        String userId = "test-user-id";

        when(shortURLRepository.findByLongUrl(longUrl)).thenThrow(new RuntimeException("Database unavailable"));

        assertThrows(RuntimeException.class, () -> {
            shortenerService.shortenUrl(longUrl, userId, null, null);
        });
    }
}