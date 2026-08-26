package com.krushiadhaar.disease.provider;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;
@Component
public class FileSystemStorageProvider implements ObjectStorageService {
    @Override
    public String storeFile(MultipartFile file) {
        // Validate MIME type, size, etc.
        // Return an opaque reference, not an absolute path.
        return "local-" + UUID.randomUUID().toString() + ".jpg";
    }
}
