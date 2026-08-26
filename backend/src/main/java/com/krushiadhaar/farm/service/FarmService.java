package com.krushiadhaar.farm.service;
import com.krushiadhaar.common.exception.UnauthorizedResourceAccessException;
import com.krushiadhaar.farm.entity.Farm;
import com.krushiadhaar.farm.repository.FarmRepository;
import com.krushiadhaar.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class FarmService {
    private final FarmRepository farmRepository;
    private final UserRepository userRepository;

    public FarmService(FarmRepository farmRepository, UserRepository userRepository) {
        this.farmRepository = farmRepository;
        this.userRepository = userRepository;
    }

    public Farm createFarm(UUID ownerId, Farm farm) {
        farm.setOwner(userRepository.findById(ownerId).orElseThrow());
        return farmRepository.save(farm);
    }

    public List<Farm> getFarms(UUID ownerId) {
        return farmRepository.findByOwnerIdAndStatus(ownerId, "ACTIVE");
    }

    public Farm getFarm(UUID id, UUID ownerId) {
        return farmRepository.findByIdAndOwnerId(id, ownerId)
            .orElseThrow(() -> new UnauthorizedResourceAccessException("Farm not found or access denied"));
    }

    public Farm updateFarm(UUID id, UUID ownerId, Farm updates) {
        Farm existing = getFarm(id, ownerId);
        existing.setName(updates.getName());
        existing.setTotalArea(updates.getTotalArea());
        return farmRepository.save(existing);
    }

    public void archiveFarm(UUID id, UUID ownerId) {
        Farm existing = getFarm(id, ownerId);
        existing.setStatus("ARCHIVED");
        farmRepository.save(existing);
    }
}
