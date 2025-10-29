package com.urlshortner.readservice.service;

import com.urlshortner.readservice.model.ShortURL;
import com.urlshortner.readservice.repository.ShortURLRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.StreamOperations;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedirectServiceTest {

    @Mock
    private ShortURLRepository shortURLRepository;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private StreamOperations<String, Object, Object> streamOperations;

    @InjectMocks
    private RedirectService redirectService;

    @Test
    void testRedisCacheHit() {
        String shortUrl = "test123";
        String longUrl = "https://www.example.com";
        String userAgent = "Mozilla/5.0";
        String cacheKey = "url:" + shortUrl;

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForStream()).thenReturn(streamOperations);
        when(valueOperations.get(cacheKey)).thenReturn(longUrl);

        String result = redirectService.getLongUrl(shortUrl, userAgent);

        assertEquals(longUrl, result);
        verify(shortURLRepository, never()).findById(shortUrl);
        verify(valueOperations).get(cacheKey);
    }

    @Test
    void testRedisCacheMiss() {
        String shortUrl = "test123";
        String longUrl = "https://www.example.com";
        String userAgent = "Mozilla/5.0";
        String cacheKey = "url:" + shortUrl;

        ShortURL shortURLEntity = new ShortURL();
        shortURLEntity.setShortUrl(shortUrl);
        shortURLEntity.setLongUrl(longUrl);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForStream()).thenReturn(streamOperations);
        when(valueOperations.get(cacheKey)).thenReturn(null);
        when(shortURLRepository.findById(shortUrl)).thenReturn(Optional.of(shortURLEntity));

        String result = redirectService.getLongUrl(shortUrl, userAgent);

        assertEquals(longUrl, result);
        verify(valueOperations).get(cacheKey);
        verify(shortURLRepository).findById(shortUrl);
        verify(valueOperations).set(cacheKey, longUrl);
    }

    @Test
    void testClickEventCreation() {
        String shortUrl = "test123";
        String longUrl = "https://www.example.com";
        String userAgent = "Mozilla/5.0";
        String cacheKey = "url:" + shortUrl;

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForStream()).thenReturn(streamOperations);
        when(valueOperations.get(cacheKey)).thenReturn(longUrl);

        redirectService.getLongUrl(shortUrl, userAgent);

        verify(streamOperations).add(eq("clicks-stream"), any());
    }

    @Test
    void testConcurrentAccess() throws Exception {
        String shortUrl = "test123";
        String longUrl = "https://www.example.com";
        String userAgent = "Mozilla/5.0";
        String cacheKey = "url:" + shortUrl;

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForStream()).thenReturn(streamOperations);
        when(valueOperations.get(cacheKey)).thenReturn(longUrl);

        ExecutorService executor = Executors.newFixedThreadPool(5);
        List<CompletableFuture<String>> futures = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            futures.add(CompletableFuture.supplyAsync(() -> 
                redirectService.getLongUrl(shortUrl, userAgent), executor));
        }

        for (CompletableFuture<String> future : futures) {
            assertEquals(longUrl, future.get());
        }

        executor.shutdown();
    }

    @Test
    void testRedisUnavailable() {
        String shortUrl = "test123";
        String longUrl = "https://www.example.com";
        String userAgent = "Mozilla/5.0";

        ShortURL shortURLEntity = new ShortURL();
        shortURLEntity.setShortUrl(shortUrl);
        shortURLEntity.setLongUrl(longUrl);

        when(redisTemplate.opsForValue()).thenThrow(new RuntimeException("Redis unavailable"));
        when(shortURLRepository.findById(shortUrl)).thenReturn(Optional.of(shortURLEntity));

        String result = redirectService.getLongUrl(shortUrl, userAgent);

        assertEquals(longUrl, result);
        verify(shortURLRepository).findById(shortUrl);
    }

    @Test
    void testDatabaseUnavailable() {
        String shortUrl = "test123";
        String userAgent = "Mozilla/5.0";
        String cacheKey = "url:" + shortUrl;

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(cacheKey)).thenReturn(null);
        when(shortURLRepository.findById(shortUrl)).thenThrow(new RuntimeException("Database unavailable"));

        assertThrows(RuntimeException.class, () -> {
            redirectService.getLongUrl(shortUrl, userAgent);
        });
    }
}