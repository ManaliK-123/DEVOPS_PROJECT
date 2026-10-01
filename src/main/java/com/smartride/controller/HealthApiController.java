package com.smartride.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthApiController {

    @GetMapping("/api/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "HEALTHY");
        health.put("application", "SmartRide Demonstration Platform");
        health.put("version", "1.1.0");
        health.put("timestamp", LocalDateTime.now().toString());
        health.put("environment", System.getProperty("spring.profiles.active", "default"));
        return ResponseEntity.ok(health);
    }
}
