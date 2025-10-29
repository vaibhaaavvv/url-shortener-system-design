package com.urlshortner.writeservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.urlshortner.writeservice.dto.response.UserInitResponse;
import com.urlshortner.writeservice.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/init")
    public ResponseEntity<UserInitResponse> initializeUser(HttpServletResponse response) {
        String userId = userService.generateUserId();
        
        // Set cookie with 1 year expiration
        Cookie userCookie = new Cookie("userId", userId);
        userCookie.setMaxAge(365 * 24 * 60 * 60);
        userCookie.setHttpOnly(false); // Allow js access
        userCookie.setPath("/");
        userCookie.setSecure(false); // Allow HTTP for development
        userCookie.setAttribute("SameSite", "Lax");
        response.addCookie(userCookie);
        
        UserInitResponse responseBody = new UserInitResponse(userId, "User initialized successfully");
        return ResponseEntity.ok(responseBody);
    }
}