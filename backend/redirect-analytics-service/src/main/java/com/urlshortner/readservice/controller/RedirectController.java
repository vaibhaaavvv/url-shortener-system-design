package com.urlshortner.readservice.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.urlshortner.readservice.dto.response.UrlListResponse;
import com.urlshortner.readservice.service.RedirectService;
import com.urlshortner.readservice.service.UrlListService;

@RestController
@RequestMapping("/api/v1")
public class RedirectController {

    @Autowired
    private RedirectService redirectService;
    
    @Autowired
    private UrlListService urlListService;

    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> redirect(@PathVariable String shortUrl, 
                                       @RequestHeader(value = "User-Agent", required = false) String userAgent) {
        String longUrl = redirectService.getLongUrl(shortUrl, userAgent);
        if (longUrl != null) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(longUrl))
                    .build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/urls")
    public ResponseEntity<UrlListResponse> getUserUrls(
            @CookieValue("userId") String userId,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int count) {
        UrlListResponse response = urlListService.getUserUrls(userId, pageNumber, count);
        return ResponseEntity.ok(response);
    }
    
}