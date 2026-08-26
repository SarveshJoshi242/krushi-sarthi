package com.krushiadhaar.notification.controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import java.util.UUID;
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private UUID getUserId() { return UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName()); }
    @GetMapping
    public ResponseEntity<String> getNotifications() { return ResponseEntity.ok("notifications"); }
}
