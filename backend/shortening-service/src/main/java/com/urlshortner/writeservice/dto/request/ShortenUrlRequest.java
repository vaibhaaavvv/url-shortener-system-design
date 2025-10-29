package com.urlshortner.writeservice.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import org.hibernate.validator.constraints.URL;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ShortenUrlRequest {
    
    @NotNull
    @URL(message = "Please enter a valid URL")
    private String longUrl;
    
    private String customAlias;
    
    @Future(message = "Please select a future date for expiration")
    private LocalDateTime expirationDate;
}