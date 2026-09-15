package com.krushiadhaar.disease.provider;
import org.springframework.web.multipart.MultipartFile;
public interface ObjectStorageService {
    String storeFile(MultipartFile file);
}
