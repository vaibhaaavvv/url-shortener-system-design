package com.urlshortner.writeservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.urlshortner.writeservice.dto.request.ShortenUrlRequest;
import com.urlshortner.writeservice.dto.response.ShortenUrlResponse;
import com.urlshortner.writeservice.service.ShortenerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class ShortnerController {

    private final ShortenerService shortenerService;

    @Autowired
    public ShortnerController(ShortenerService shortenerService) {
        this.shortenerService = shortenerService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<ShortenUrlResponse> shortenUrl(@Valid @RequestBody ShortenUrlRequest request, @CookieValue("userId") String userId) {
        ShortenUrlResponse response = shortenerService.shortenUrl(request.getLongUrl(), userId, request.getCustomAlias(), request.getExpirationDate());
        if (response.getShortUrl() == null) {
            return ResponseEntity.badRequest().body(new ShortenUrlResponse(response.getMessage(), null));
        }
        response = new ShortenUrlResponse(
            "Short URL processed successfully", 
            response.getShortUrl()
        );
        return ResponseEntity.ok(response);
    }
}
