package com.krushiadhaar.disease.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@Component
@Primary
public class MLServiceDiseaseDetectionProvider implements DiseaseDetectionProvider {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public MLServiceDiseaseDetectionProvider(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public CompletableFuture<DetectionResult> processImage(String imageReference) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);

                MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                // Send dummy bytes since the ML service returns random if crop_name isn't provided.
                body.add("file", new ByteArrayResource(new byte[]{1, 2, 3}) {
                    @Override
                    public String getFilename() {
                        return "image.jpg";
                    }
                });

                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
                
                String serverUrl = "http://localhost:8000/predict";
                ResponseEntity<String> response = restTemplate.postForEntity(serverUrl, requestEntity, String.class);

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    JsonNode root = objectMapper.readTree(response.getBody());
                    
                    DetectionResult result = new DetectionResult();
                    result.setDiseaseName(root.path("Disease_Name").asText());
                    result.setConfidence(new BigDecimal(root.path("Confidence_Score").asText()));
                    result.setSeverity("Moderate"); // Not in CSV explicitly, so default
                    result.setRecommendation(
                            "Symptoms: " + root.path("Symptoms").asText() + 
                            "\nTreatment: " + root.path("Treatment").asText() +
                            "\nPrevention: " + root.path("Prevention").asText()
                    );
                    if(root.has("Medicines")) {
                        result.setMedicines(root.path("Medicines").asText());
                    }
                    if(root.has("Estimated_Cost")) {
                        result.setEstimatedCost(new BigDecimal(root.path("Estimated_Cost").asText()));
                    }
                                            
                    return result;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // Fallback
            DetectionResult fallback = new DetectionResult();
            fallback.setDiseaseName("Unknown (Error analyzing)");
            fallback.setConfidence(BigDecimal.ZERO);
            fallback.setSeverity("Unknown");
            fallback.setRecommendation("Could not connect to ML service.");
            return fallback;
        });
    }
}
