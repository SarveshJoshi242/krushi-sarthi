package com.krushiadhaar.location.controller;

import com.krushiadhaar.location.service.LocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/location")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping("/distance")
    public ResponseEntity<Map<String, Object>> calculateDistance(
            @RequestParam double lat1, @RequestParam double lon1,
            @RequestParam double lat2, @RequestParam double lon2) {
        double distance = locationService.calculateDistance(lat1, lon1, lat2, lon2);
        return ResponseEntity.ok(Map.of(
                "distance_km", Math.round(distance * 100.0) / 100.0,
                "from", Map.of("lat", lat1, "lon", lon1),
                "to", Map.of("lat", lat2, "lon", lon2)
        ));
    }
}
