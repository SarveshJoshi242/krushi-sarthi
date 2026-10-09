package com.krushiadhaar.farmintelligence;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CropRecommendationDto {
    private String rank;
    private String emoji;
    private String cropName;
    private String suitability;
    private String netReturn;
    private String waterRequirement;
    private String sowingPeriod;
    private String soilNutrientsRequired;
    private String farmingTechnique;
}
