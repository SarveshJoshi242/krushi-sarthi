package com.krushiadhaar.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class PingController {

    @GetMapping({"/ping", "/api/ping", "/api/health/ping"})
    public ResponseEntity<Map<String, Object>> ping() {
        return ResponseEntity.ok(Map.of(
                "status", "pong",
                "service", "krushi-backend",
                "timestamp", System.currentTimeMillis()
        ));
    }
}
