package com.urlshortner.readservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.urlshortner.readservice.repository.ShortURLRepository;
import java.time.LocalDateTime;

@Service
public class ExpirationCleanupService {

    @Autowired
    private ShortURLRepository shortURLRepository;

    @Scheduled(fixedRate = 3000000)
    public void deleteExpiredUrls() {
        LocalDateTime now = LocalDateTime.now();
        int deletedCount = shortURLRepository.deleteByExpirationDateBefore(now);
        if (deletedCount > 0) {
            System.out.println("Deleted " + deletedCount + " expired URLs at " + now);
        }
    }
}