package com.krushiadhaar.disease.provider;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;
import java.math.BigDecimal;

@Component
public class MockDiseaseDetectionProvider implements DiseaseDetectionProvider {
    @Async
    @Override
    public CompletableFuture<DetectionResult> processImage(String imageReference) {
        try {
            Thread.sleep(2000); // simulate ML processing
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        DetectionResult result = new DetectionResult();
        result.setDiseaseName("Leaf Blight");
        result.setConfidence(new BigDecimal("94.50"));
        result.setSeverity("Moderate");
        result.setRecommendation("Apply recommended fungicide and avoid overhead irrigation.");
        return CompletableFuture.completedFuture(result);
    }
}
