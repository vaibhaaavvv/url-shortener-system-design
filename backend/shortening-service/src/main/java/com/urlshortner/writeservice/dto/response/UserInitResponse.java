package com.urlshortner.writeservice.dto.response;

import lombok.Data;

@Data
public class UserInitResponse {
    private String userId;
    private String message;
    
    public UserInitResponse(String userId, String message) {
        this.userId = userId;
        this.message = message;
    }
}