package com.krushiadhaar.disease.service;
import com.krushiadhaar.common.exception.UnauthorizedResourceAccessException;
import com.krushiadhaar.disease.entity.DiseaseScan;
import com.krushiadhaar.disease.entity.DiseaseResult;
import com.krushiadhaar.disease.repository.DiseaseScanRepository;
import com.krushiadhaar.disease.repository.DiseaseResultRepository;
import com.krushiadhaar.disease.provider.ObjectStorageService;
import com.krushiadhaar.disease.provider.DiseaseDetectionProvider;
import com.krushiadhaar.crop.repository.CropCycleRepository;
import com.krushiadhaar.crop.entity.CropCycle;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;
import java.util.List;
import java.util.Optional;

@Service
public class DiseaseService {
    private final DiseaseScanRepository scanRepository;
    private final DiseaseResultRepository resultRepository;
    private final CropCycleRepository cropCycleRepository;
    private final ObjectStorageService storageService;
    private final DiseaseDetectionProvider detectionProvider;

    public DiseaseService(DiseaseScanRepository scanRepository, DiseaseResultRepository resultRepository,
                          CropCycleRepository cropCycleRepository, ObjectStorageService storageService,
                          DiseaseDetectionProvider detectionProvider) {
        this.scanRepository = scanRepository;
        this.resultRepository = resultRepository;
        this.cropCycleRepository = cropCycleRepository;
        this.storageService = storageService;
        this.detectionProvider = detectionProvider;
    }

    @Transactional
    public DiseaseScan createScan(UUID userId, UUID cropCycleId, MultipartFile file) {
        // Deep Ownership Check
        CropCycle cycle = cropCycleRepository.findByIdAndFieldFarmOwnerId(cropCycleId, userId)
            .orElseThrow(() -> new UnauthorizedResourceAccessException("Crop cycle not found or access denied"));

        String imageRef = storageService.storeFile(file);
        
        DiseaseScan scan = DiseaseScan.builder()
            .userId(userId)
            .farmId(cycle.getField().getFarm().getId())
            .fieldId(cycle.getField().getId())
            .cropCycleId(cycle.getId())
            .imageReference(imageRef)
            .status("PENDING")
            .build();
        
        DiseaseScan savedScan = scanRepository.save(scan);
        
        // Start async processing
        processScanAsync(savedScan.getId());
        
        return savedScan;
    }

    private void processScanAsync(UUID scanId) {
        DiseaseScan scan = scanRepository.findById(scanId).orElseThrow();
        scan.setStatus("PROCESSING");
        scanRepository.save(scan);

        detectionProvider.processImage(scan.getImageReference()).thenAccept(result -> {
            DiseaseScan updatedScan = scanRepository.findById(scanId).orElseThrow();
            updatedScan.setStatus("COMPLETED");
            scanRepository.save(updatedScan);

            DiseaseResult dbResult = DiseaseResult.builder()
                .scanId(updatedScan.getId())
                .diseaseName(result.getDiseaseName())
                .confidence(result.getConfidence())
                .severity(result.getSeverity())
                .recommendation(result.getRecommendation())
                .build();
            resultRepository.save(dbResult);
        }).exceptionally(ex -> {
            DiseaseScan failedScan = scanRepository.findById(scanId).orElseThrow();
            failedScan.setStatus("FAILED");
            scanRepository.save(failedScan);
            return null;
        });
    }

    public List<DiseaseScan> getUserScans(UUID userId) {
        return scanRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public ScanStatusResponse getScanStatus(UUID scanId, UUID userId) {
        DiseaseScan scan = scanRepository.findByIdAndUserId(scanId, userId)
            .orElseThrow(() -> new UnauthorizedResourceAccessException("Scan not found or access denied"));
        
        ScanStatusResponse response = new ScanStatusResponse();
        response.setScan(scan);
        
        if ("COMPLETED".equals(scan.getStatus())) {
            resultRepository.findByScanId(scan.getId()).ifPresent(response::setResult);
        }
        return response;
    }
}
