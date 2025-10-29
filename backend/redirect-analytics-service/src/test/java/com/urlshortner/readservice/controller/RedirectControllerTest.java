package com.urlshortner.readservice.controller;

import com.urlshortner.readservice.service.RedirectService;
import com.urlshortner.readservice.service.UrlListService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RedirectController.class)
class RedirectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RedirectService redirectService;
    
    @MockBean
    private UrlListService urlListService;

    @Test
    void testValidShortUrlRedirection() throws Exception {
        String shortUrl = "test123";
        String longUrl = "https://www.test.com";
        String userAgent = "Mozilla/5.0";

        when(redirectService.getLongUrl(shortUrl, userAgent)).thenReturn(longUrl);

        mockMvc.perform(get("/api/v1/" + shortUrl)
                .header("User-Agent", userAgent))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", longUrl));
    }

    @Test
    void testInvalidShortUrlHandling() throws Exception {
        String shortUrl = "invalid";
        String userAgent = "Mozilla/5.0";

        when(redirectService.getLongUrl(shortUrl, userAgent)).thenReturn(null);

        mockMvc.perform(get("/api/v1/" + shortUrl)
                .header("User-Agent", userAgent))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCaseSensitivity() throws Exception {
        String shortUrlLower = "test123";
        String shortUrlUpper = "TEST123";
        String longUrl = "https://www.test.com";
        String userAgent = "Mozilla/5.0";

        when(redirectService.getLongUrl(shortUrlLower, userAgent)).thenReturn(longUrl);
        when(redirectService.getLongUrl(shortUrlUpper, userAgent)).thenReturn(null);

        // Test lowercase returns URL
        mockMvc.perform(get("/api/v1/" + shortUrlLower)
                .header("User-Agent", userAgent))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", longUrl));

        // Test uppercase returns 404
        mockMvc.perform(get("/api/v1/" + shortUrlUpper)
                .header("User-Agent", userAgent))
                .andExpect(status().isNotFound());
    }
}