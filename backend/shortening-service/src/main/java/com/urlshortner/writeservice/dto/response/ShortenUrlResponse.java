package com.urlshortner.writeservice.dto.response;

import lombok.Data;

@Data
public class ShortenUrlResponse {
    private String message;
    private String shortUrl;
    
    public ShortenUrlResponse(String message, String shortUrl) {
        this.message = message;
        this.shortUrl = shortUrl;
    }
}