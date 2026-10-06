package com.krushiadhaar.document.controller;

import com.krushiadhaar.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {

    private UUID getUserId() {
        return UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getDocuments() {
        return ResponseEntity.ok(ApiResponse.success(Collections.emptyList()));
    }
}
