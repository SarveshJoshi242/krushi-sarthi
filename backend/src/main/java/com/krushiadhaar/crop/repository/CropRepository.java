package com.krushiadhaar.crop.repository;
import com.krushiadhaar.crop.entity.Crop;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface CropRepository extends JpaRepository<Crop, UUID> {}
