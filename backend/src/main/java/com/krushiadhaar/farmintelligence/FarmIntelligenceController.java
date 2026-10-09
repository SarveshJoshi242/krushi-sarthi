package com.krushiadhaar.farmintelligence;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1/farm-intelligence")
public class FarmIntelligenceController {

    @GetMapping("/recommendations")
    public ResponseEntity<List<CropRecommendationDto>> getCropRecommendations(
            @RequestParam(required = false, defaultValue = "Clear") String weatherCondition,
            @RequestParam(required = false, defaultValue = "October") String currentMonth
    ) {
        List<CropRecommendationDto> recommendations = new ArrayList<>();
        
        // Dynamic Recommendation based on time/weather (simple logic)
        if (currentMonth.equalsIgnoreCase("October") || currentMonth.equalsIgnoreCase("November")) {
            recommendations.add(CropRecommendationDto.builder()
                .rank("1")
                .emoji("🌱")
                .cropName("Rapeseed & Mustard")
                .suitability("82% Suitable")
                .netReturn("Rs. 9,450/ha")
                .waterRequirement("9755 m³")
                .sowingPeriod("October - November")
                .soilNutrientsRequired("Nitrogen (N): High, Phosphorus (P): Medium")
                .farmingTechnique("Line sowing with proper spacing. Use integrated pest management.")
                .build());
            
            recommendations.add(CropRecommendationDto.builder()
                .rank("2")
                .emoji("🌿")
                .cropName("Soybean")
                .suitability("75% Suitable")
                .netReturn("Rs. 7,200/ha")
                .waterRequirement("6000 m³")
                .sowingPeriod("October - November")
                .soilNutrientsRequired("Phosphorus (P): High, Potassium (K): Low")
                .farmingTechnique("Seed inoculation with Rhizobium before sowing.")
                .build());
                
            recommendations.add(CropRecommendationDto.builder()
                .rank("3")
                .emoji("🌾")
                .cropName("Wheat")
                .suitability("70% Suitable")
                .netReturn("Rs. 10,000/ha")
                .waterRequirement("8500 m³")
                .sowingPeriod("November")
                .soilNutrientsRequired("Nitrogen (N): High, Potassium (K): Medium")
                .farmingTechnique("Pre-sowing irrigation is required for optimal germination.")
                .build());
        } else {
             recommendations.add(CropRecommendationDto.builder()
                .rank("1")
                .emoji("🌽")
                .cropName("Maize")
                .suitability("88% Suitable")
                .netReturn("Rs. 8,500/ha")
                .waterRequirement("7000 m³")
                .sowingPeriod("June - July")
                .soilNutrientsRequired("Nitrogen (N): High, Zinc (Zn): Medium")
                .farmingTechnique("Ridge and furrow method of sowing.")
                .build());
                
             recommendations.add(CropRecommendationDto.builder()
                .rank("2")
                .emoji("🌾")
                .cropName("Paddy (Rice)")
                .suitability("85% Suitable")
                .netReturn("Rs. 12,000/ha")
                .waterRequirement("12000 m³")
                .sowingPeriod("June - July")
                .soilNutrientsRequired("Nitrogen (N): High, Iron (Fe): Medium")
                .farmingTechnique("Transplanting method in puddled field.")
                .build());
        }
        
        return ResponseEntity.ok(recommendations);
    }
}
