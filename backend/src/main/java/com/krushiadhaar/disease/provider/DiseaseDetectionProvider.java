package com.krushiadhaar.disease.provider;
import java.util.concurrent.CompletableFuture;
import java.util.UUID;
public interface DiseaseDetectionProvider {
    CompletableFuture<DetectionResult> processImage(String imageReference);
}
