package com.krushiadhaar.farmintelligence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v1/farm-intelligence")
@RequiredArgsConstructor
public class FarmIntelligenceController {

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();
    
    // Simple in-memory cache
    private final Map<String, List<CropRecommendationDto>> cache = new ConcurrentHashMap<>();
    private final Map<String, String> schemeCache = new ConcurrentHashMap<>();
    private final Map<String, String> expenseCache = new ConcurrentHashMap<>();

    @GetMapping("/recommendations")
    public ResponseEntity<List<CropRecommendationDto>> getCropRecommendations(
            @RequestParam(required = false, defaultValue = "Clear") String weatherCondition,
            @RequestParam(required = false, defaultValue = "October") String currentMonth,
            @RequestParam(required = false, defaultValue = "India") String location,
            @RequestParam(required = false, defaultValue = "English") String language
    ) {
        String cacheKey = weatherCondition + "_" + currentMonth + "_" + location + "_" + language;
        if (cache.containsKey(cacheKey)) {
            return ResponseEntity.ok(cache.get(cacheKey));
        }

        try {
            String url = String.format("http://localhost:8000/recommendations?weatherCondition=%s&currentMonth=%s&location=%s&language=%s", 
                weatherCondition, currentMonth, location, language);
            String json = restTemplate.getForObject(url, String.class);
            List<CropRecommendationDto> recommendations = objectMapper.readValue(json, new TypeReference<List<CropRecommendationDto>>() {});
            cache.put(cacheKey, recommendations);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(getDefaultRecommendations(currentMonth));
        }
    }

    @GetMapping("/schemes")
    public ResponseEntity<String> getGovernmentSchemes(
            @RequestParam(required = false, defaultValue = "India") String location,
            @RequestParam(required = false, defaultValue = "Wheat") String crop,
            @RequestParam(required = false, defaultValue = "English") String language
    ) {
        String cacheKey = location + "_" + crop + "_" + language;
        if (schemeCache.containsKey(cacheKey)) {
            return ResponseEntity.ok(schemeCache.get(cacheKey));
        }

        try {
            String url = String.format("http://localhost:8000/schemes?location=%s&crop=%s&language=%s", location, crop, language);
            String json = restTemplate.getForObject(url, String.class);
            schemeCache.put(cacheKey, json);
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(getDefaultSchemes());
        }
    }

    @GetMapping("/expenses")
    public ResponseEntity<String> getExpenseTracker(
            @RequestParam(required = false, defaultValue = "Wheat") String crop,
            @RequestParam(required = false, defaultValue = "1 acre") String area,
            @RequestParam(required = false, defaultValue = "English") String language
    ) {
        String cacheKey = crop + "_" + area + "_" + language;
        if (expenseCache.containsKey(cacheKey)) {
            return ResponseEntity.ok(expenseCache.get(cacheKey));
        }

        try {
            String url = String.format("http://localhost:8000/expenses?crop=%s&area=%s&language=%s", crop, area, language);
            String json = restTemplate.getForObject(url, String.class);
            expenseCache.put(cacheKey, json);
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok("[]");
        }
    }

    private String getDefaultSchemes() {
        return "[{\"title\":\"PM-KISAN\",\"provider\":\"Central Government\",\"description\":\"Income support of Rs. 6,000 per year.\",\"isActive\":true,\"link\":\"https://pmkisan.gov.in/\",\"requiredDocuments\":[\"Aadhaar Card\",\"Bank Account Details\"],\"type\":\"Scheme\"}," +
               "{\"title\":\"Pradhan Mantri Fasal Bima Yojana\",\"provider\":\"Central Government\",\"description\":\"Crop insurance scheme.\",\"isActive\":true,\"link\":\"https://pmfby.gov.in/\",\"requiredDocuments\":[\"Land Records\",\"Aadhaar Card\"],\"type\":\"Insurance\"}]";
    }

    private List<CropRecommendationDto> getDefaultRecommendations(String currentMonth) {
        List<CropRecommendationDto> recommendations = new ArrayList<>();
        if (currentMonth.equalsIgnoreCase("October") || currentMonth.equalsIgnoreCase("November")) {
            recommendations.add(CropRecommendationDto.builder()
                .rank("1").emoji("🌱").cropName("Rapeseed & Mustard").suitability("82% Suitable").netReturn("Rs. 9,450/ha")
                .waterRequirement("9755 m³").sowingPeriod("October - November").soilNutrientsRequired("Nitrogen (N): High").farmingTechnique("Line sowing.")
                .build());
        } else {
             recommendations.add(CropRecommendationDto.builder()
                .rank("1").emoji("🌽").cropName("Maize").suitability("88% Suitable").netReturn("Rs. 8,500/ha")
                .waterRequirement("7000 m³").sowingPeriod("June - July").soilNutrientsRequired("Nitrogen (N): High").farmingTechnique("Ridge and furrow.")
                .build());
        }
        return recommendations;
    }
}
