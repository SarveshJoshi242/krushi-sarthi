package com.krushiadhaar.farm.controller;

import com.krushiadhaar.farm.entity.Field;
import com.krushiadhaar.farm.service.FieldService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/farms/{farmId}/fields")
public class FieldController {
    private final FieldService fieldService;

    public FieldController(FieldService fieldService) {
        this.fieldService = fieldService;
    }

    private UUID getUserId() {
        return UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PostMapping
    public ResponseEntity<Field> createField(@PathVariable UUID farmId, @RequestBody Field field) {
        return ResponseEntity.ok(fieldService.createField(farmId, getUserId(), field));
    }

    @GetMapping
    public ResponseEntity<List<Field>> getFields(@PathVariable UUID farmId) {
        return ResponseEntity.ok(fieldService.getFields(farmId, getUserId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Field> getField(@PathVariable("farmId") UUID farmId, @PathVariable("id") UUID id) {
        return ResponseEntity.ok(fieldService.getField(id, getUserId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> archiveField(@PathVariable("farmId") UUID farmId, @PathVariable("id") UUID id) {
        fieldService.archiveField(id, getUserId());
        return ResponseEntity.noContent().build();
    }
}
