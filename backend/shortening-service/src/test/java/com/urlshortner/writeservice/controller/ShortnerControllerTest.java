package com.urlshortner.writeservice.controller;

import com.urlshortner.writeservice.dto.response.ShortenUrlResponse;
import com.urlshortner.writeservice.service.ShortenerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShortnerController.class)
class ShortnerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShortenerService shortenerService;

    @Test
    void testValidUrlShortening() throws Exception {
        String longUrl = "https://www.example.com";
        String userId = "test-user-id";
        String shortUrl = "abc123";

        when(shortenerService.shortenUrl(eq(longUrl), eq(userId), any(), any()))
            .thenReturn(new ShortenUrlResponse("Short URL created successfully", shortUrl));

        String requestBody = "{\"longUrl\":\"" + longUrl + "\"}";

        mockMvc.perform(post("/api/v1/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .cookie(new jakarta.servlet.http.Cookie("userId", userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortUrl").value(shortUrl))
                .andExpect(jsonPath("$.message").value("Short URL processed successfully"));
    }

    @Test
    void testInvalidUrlFormat() throws Exception {
        String invalidUrl = "not-a-valid-url";
        String userId = "test-user-id";

        String requestBody = "{\"longUrl\":\"" + invalidUrl + "\"}";

        mockMvc.perform(post("/api/v1/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .cookie(new jakarta.servlet.http.Cookie("userId", userId)))
                .andExpect(status().isBadRequest());
    }
}