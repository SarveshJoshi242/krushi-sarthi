package com.krushiadhaar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @Autowired
    private DataSource dataSource;

    @GetMapping
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }

    @GetMapping("/db")
    public ResponseEntity<Map<String, String>> dbHealth() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(1000)) {
                return ResponseEntity.ok(Map.of(
                        "status", "UP",
                        "database", "PostgreSQL (Neon)"
                ));
            } else {
                return ResponseEntity.status(503).body(Map.of("status", "DOWN"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(503).body(Map.of("status", "DOWN"));
        }
    }
}
