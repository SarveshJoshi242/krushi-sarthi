package com.krushiadhaar.inventory.controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import java.util.UUID;
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private UUID getUserId() { return UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName()); }
    @GetMapping
    public ResponseEntity<String> getInventory() { return ResponseEntity.ok("inventory"); }
}
