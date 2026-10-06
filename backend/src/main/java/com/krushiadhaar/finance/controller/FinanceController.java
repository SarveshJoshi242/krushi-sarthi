package com.krushiadhaar.finance.controller;

import com.krushiadhaar.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/finance")
public class FinanceController {

    @GetMapping("/schemes")
    public ApiResponse<List<Map<String, String>>> getGovtSchemes() {
        return ApiResponse.success(List.of(
            Map.of("id", "SCH-001", "name", "PM-Kisan Samman Nidhi", "description", "Provides income support to all landholding farmers' families in the country.", "eligibility", "All landholding farmers' families", "link", "https://pmkisan.gov.in/"),
            Map.of("id", "SCH-002", "name", "Pradhan Mantri Fasal Bima Yojana (PMFBY)", "description", "Crop insurance scheme integrating multiple stakeholders on a single platform.", "eligibility", "All farmers growing notified crops in a notified area", "link", "https://pmfby.gov.in/"),
            Map.of("id", "SCH-003", "name", "Soil Health Card Scheme", "description", "Helps farmers get information about the nutrient status of their soil.", "eligibility", "All farmers", "link", "https://soilhealth.dac.gov.in/")
        ));
    }

    @GetMapping("/insurance")
    public ApiResponse<List<Map<String, String>>> getInsurance() {
        return ApiResponse.success(List.of(
            Map.of("id", "INS-001", "provider", "LIC", "name", "Crop Protection Plan", "coverage", "Up to ₹50,000/acre", "premium", "₹500/month"),
            Map.of("id", "INS-002", "provider", "HDFC ERGO", "name", "Weather Insurance", "coverage", "Drought & Flood protection", "premium", "₹300/month")
        ));
    }

    @GetMapping("/loans")
    public ApiResponse<List<Map<String, String>>> getLoans() {
        return ApiResponse.success(List.of(
            Map.of("id", "LOAN-001", "provider", "SBI", "name", "Kisan Credit Card (KCC)", "interestRate", "7% p.a.", "maxAmount", "₹3,00,000", "term", "Short term"),
            Map.of("id", "LOAN-002", "provider", "NABARD", "name", "Agri Infrastructure Loan", "interestRate", "5% p.a.", "maxAmount", "₹10,00,000", "term", "Long term")
        ));
    }
}
