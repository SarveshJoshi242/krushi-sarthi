package com.krushiadhaar.disease.provider;
import lombok.Data;
import java.math.BigDecimal;
@Data
public class DetectionResult {
    private String diseaseName;
    private BigDecimal confidence;
    private String severity;
    private String recommendation;
}
