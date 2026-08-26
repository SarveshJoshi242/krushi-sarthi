package com.krushiadhaar.weather.controller;

import com.krushiadhaar.weather.service.WeatherResponse;
import com.krushiadhaar.weather.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {
    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    private UUID getUserId() {
        return UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @GetMapping("/farm/{farmId}")
    public ResponseEntity<WeatherResponse> getWeather(@PathVariable UUID farmId) {
        return ResponseEntity.ok(weatherService.getWeather(farmId, getUserId()));
    }
}
