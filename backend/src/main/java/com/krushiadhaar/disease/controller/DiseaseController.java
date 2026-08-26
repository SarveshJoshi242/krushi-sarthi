package com.krushiadhaar.disease.controller;

import com.krushiadhaar.disease.entity.DiseaseScan;
import com.krushiadhaar.disease.service.DiseaseService;
import com.krushiadhaar.disease.service.ScanStatusResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/diseases")
public class DiseaseController {
    private final DiseaseService diseaseService;

    public DiseaseController(DiseaseService diseaseService) {
        this.diseaseService = diseaseService;
    }

    private UUID getUserId() {
        return UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PostMapping("/scan")
    public ResponseEntity<DiseaseScan> createScan(@RequestParam("cropCycleId") UUID cropCycleId, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(diseaseService.createScan(getUserId(), cropCycleId, file));
    }

    @GetMapping("/scans")
    public ResponseEntity<List<DiseaseScan>> getUserScans() {
        return ResponseEntity.ok(diseaseService.getUserScans(getUserId()));
    }

    @GetMapping("/scans/{id}")
    public ResponseEntity<ScanStatusResponse> getScanStatus(@PathVariable UUID id) {
        return ResponseEntity.ok(diseaseService.getScanStatus(id, getUserId()));
    }
}
