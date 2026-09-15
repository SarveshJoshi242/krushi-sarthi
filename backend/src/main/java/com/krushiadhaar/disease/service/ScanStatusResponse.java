package com.krushiadhaar.disease.service;
import com.krushiadhaar.disease.entity.DiseaseScan;
import com.krushiadhaar.disease.entity.DiseaseResult;
import lombok.Data;
@Data
public class ScanStatusResponse {
    private DiseaseScan scan;
    private DiseaseResult result;
}
