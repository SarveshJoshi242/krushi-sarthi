package com.krushiadhaar.farm.controller;

import com.krushiadhaar.farm.entity.Farm;
import com.krushiadhaar.farm.service.FarmService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/farms")
public class FarmController {
    private final FarmService farmService;

    public FarmController(FarmService farmService) {
        this.farmService = farmService;
    }

    private UUID getUserId() {
        return UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PostMapping
    public ResponseEntity<Farm> createFarm(@RequestBody Farm farm) {
        return ResponseEntity.ok(farmService.createFarm(getUserId(), farm));
    }

    @GetMapping
    public ResponseEntity<List<Farm>> getFarms() {
        return ResponseEntity.ok(farmService.getFarms(getUserId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Farm> getFarm(@PathVariable UUID id) {
        return ResponseEntity.ok(farmService.getFarm(id, getUserId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Farm> updateFarm(@PathVariable UUID id, @RequestBody Farm updates) {
        return ResponseEntity.ok(farmService.updateFarm(id, getUserId(), updates));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> archiveFarm(@PathVariable UUID id) {
        farmService.archiveFarm(id, getUserId());
        return ResponseEntity.noContent().build();
    }
}
