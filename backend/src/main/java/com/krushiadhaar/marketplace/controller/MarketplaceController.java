package com.krushiadhaar.marketplace.controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import java.util.UUID;
@RestController
@RequestMapping("/api/marketplace")
public class MarketplaceController {
    private UUID getUserId() { return UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName()); }
    @GetMapping("/listings")
    public ResponseEntity<String> getListings() { return ResponseEntity.ok("listings"); }
}
