package com.krushiadhaar.crop.controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import java.util.UUID;
@RestController
@RequestMapping("/api/crops")
public class CropController {
    private UUID getUserId() { return UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName()); }
    @GetMapping
    public ResponseEntity<String> getCrops() { return ResponseEntity.ok("crops"); }
}
