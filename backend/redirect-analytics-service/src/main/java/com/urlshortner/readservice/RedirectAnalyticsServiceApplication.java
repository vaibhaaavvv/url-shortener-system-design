package com.urlshortner.readservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RedirectAnalyticsServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RedirectAnalyticsServiceApplication.class, args);
    }
}