package com.urlshortner.readservice.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "short_urls")
@Data
public class ShortURL {
    
    @Id
    private String shortUrl;
    
    @Column(nullable = false, length = 2048)
    private String longUrl;
    
    private String userId;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime expirationDate;
}