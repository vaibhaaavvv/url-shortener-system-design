package com.urlshortner.readservice.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UrlListItem {
    private String shortenedUrl;
    private String longUrl;
    private LocalDateTime createdAt;
    
    public UrlListItem(String shortUrl, String longUrl, LocalDateTime createdAt) {
        this.shortenedUrl = "https://miniurl.com/" + shortUrl;
        this.longUrl = longUrl;
        this.createdAt = createdAt;
    }
}