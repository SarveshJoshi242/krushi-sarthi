package com.krushiadhaar.farmintelligence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("/voice-assistant")
    public ResponseEntity<String> voiceAssistant(
            @RequestParam String query,
            @RequestParam(required = false, defaultValue = "English") String language
    ) {
        try {
            org.springframework.util.MultiValueMap<String, String> map = new org.springframework.util.LinkedMultiValueMap<>();
            map.add("query", query);
            map.add("language", language);
            String url = "http://localhost:8000/voice-assistant";
            String json = restTemplate.postForObject(url, map, String.class);
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            String fallback = String.format("{\"response\":\"Advice for %s: Ensure timely irrigation and balanced fertilizer application.\",\"language\":\"%s\"}", query, language);
            return ResponseEntity.ok(fallback);
        }
    }

    @GetMapping("/mandi-prices")
    public ResponseEntity<String> getMandiPrices(
            @RequestParam(required = false, defaultValue = "Maharashtra") String location,
            @RequestParam(required = false) String commodity
    ) {
        try {
            String url = "http://localhost:8000/mandi-prices?location=" + location + (commodity != null ? "&commodity=" + commodity : "");
            String json = restTemplate.getForObject(url, String.class);
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            return ResponseEntity.ok("[]");
        }
    }

    @PostMapping("/soil-health")
    public ResponseEntity<String> calculateSoilHealth(
            @RequestParam(defaultValue = "210.0") double nitrogen,
            @RequestParam(defaultValue = "18.0") double phosphorus,
            @RequestParam(defaultValue = "220.0") double potassium,
            @RequestParam(defaultValue = "7.2") double ph,
            @RequestParam(defaultValue = "1.0") double acreage,
            @RequestParam(defaultValue = "Wheat") String crop
    ) {
        try {
            org.springframework.util.MultiValueMap<String, String> map = new org.springframework.util.LinkedMultiValueMap<>();
            map.add("nitrogen", String.valueOf(nitrogen));
            map.add("phosphorus", String.valueOf(phosphorus));
            map.add("potassium", String.valueOf(potassium));
            map.add("ph", String.valueOf(ph));
            map.add("acreage", String.valueOf(acreage));
            map.add("crop", crop);
            String url = "http://localhost:8000/soil-health";
            String json = restTemplate.postForObject(url, map, String.class);
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            return ResponseEntity.ok("{\"error\":\"Soil calculation service temporarily unavailable\"}");
        }
    }

    @GetMapping("/irrigation-calendar")
    public ResponseEntity<String> getIrrigationCalendar(
            @RequestParam(defaultValue = "Wheat") String crop,
            @RequestParam(defaultValue = "28") int sowingDays
    ) {
        try {
            String url = String.format("http://localhost:8000/irrigation-calendar?crop=%s&sowing_days=%d", crop, sowingDays);
            String json = restTemplate.getForObject(url, String.class);
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            return ResponseEntity.ok("{\"error\":\"Irrigation calendar service temporarily unavailable\"}");
        }
    }

    private String getDefaultSchemes() {
        return "[{\"title\":\"PM-KISAN\",\"provider\":\"Central Government\",\"description\":\"Income support of Rs. 6,000 per year.\",\"isActive\":true,\"link\":\"https://pmkisan.gov.in/\",\"requiredDocuments\":[\"Aadhaar Card\",\"Bank Account Details\"],\"type\":\"Scheme\"}," +
               "{\"title\":\"Pradhan Mantri Fasal Bima Yojana\",\"provider\":\"Central Government\",\"description\":\"Crop insurance scheme.\",\"isActive\":true,\"link\":\"https://pmfby.gov.in/\",\"requiredDocuments\":[\"Land Records\",\"Aadhaar Card\"],\"type\":\"Insurance\"}]";
    }

    private List<CropRecommendationDto> getDefaultRecommendations(String currentMonth) {
        List<CropRecommendationDto> recommendations = new ArrayList<>();
        recommendations.add(CropRecommendationDto.builder()
            .rank("1").emoji("🌾").cropName("Wheat (HD-2967)").suitability("94% Suitable").netReturn("Rs. 48,500/ha")
            .waterRequirement("4200 m³").sowingPeriod("October - November").soilNutrientsRequired("Nitrogen: 120, Phosphorus: 60, Potash: 40 kg/ha").farmingTechnique("Zero tillage line sowing with seed-cum-fertilizer drill.")
            .build());
        recommendations.add(CropRecommendationDto.builder()
            .rank("2").emoji("🌱").cropName("Mustard (Pusa 25)").suitability("89% Suitable").netReturn("Rs. 42,000/ha")
            .waterRequirement("2800 m³").sowingPeriod("October - November").soilNutrientsRequired("Nitrogen: 80, Phosphorus: 40, Sulfur: 25 kg/ha").farmingTechnique("Broad bed & furrow (BBF) method at 45cm spacing.")
            .build());
        recommendations.add(CropRecommendationDto.builder()
            .rank("3").emoji("🫘").cropName("Chickpea (JG-11)").suitability("85% Suitable").netReturn("Rs. 38,500/ha")
            .waterRequirement("2400 m³").sowingPeriod("October - December").soilNutrientsRequired("Rhizobium culture, Phosphorus: 50 kg/ha").farmingTechnique("Ridge and furrow conservation furrow method.")
            .build());
        return recommendations;
    }
}
