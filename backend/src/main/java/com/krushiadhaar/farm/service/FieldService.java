package com.krushiadhaar.farm.service;

import com.krushiadhaar.common.exception.DomainException;
import com.krushiadhaar.common.exception.UnauthorizedResourceAccessException;
import com.krushiadhaar.farm.entity.Field;
import com.krushiadhaar.farm.entity.Farm;
import com.krushiadhaar.farm.repository.FieldRepository;
import com.krushiadhaar.farm.repository.FarmRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class FieldService {
    private final FieldRepository fieldRepository;
    private final FarmRepository farmRepository;

    public FieldService(FieldRepository fieldRepository, FarmRepository farmRepository) {
        this.fieldRepository = fieldRepository;
        this.farmRepository = farmRepository;
    }

    public Field createField(UUID farmId, UUID ownerId, Field field) {
        Farm farm = farmRepository.findByIdAndOwnerId(farmId, ownerId)
            .orElseThrow(() -> new UnauthorizedResourceAccessException("Farm not found or access denied"));
        field.setFarm(farm);
        return fieldRepository.save(field);
    }

    public List<Field> getFields(UUID farmId, UUID ownerId) {
        Farm farm = farmRepository.findByIdAndOwnerId(farmId, ownerId)
            .orElseThrow(() -> new DomainException("Access denied"));
        return fieldRepository.findByFarmIdAndStatus(farm.getId(), "ACTIVE");
    }

    public Field getField(UUID id, UUID ownerId) {
        return fieldRepository.findByIdAndFarmOwnerId(id, ownerId)
            .orElseThrow(() -> new UnauthorizedResourceAccessException("Field not found or access denied"));
    }

    public void archiveField(UUID id, UUID ownerId) {
        Field existing = getField(id, ownerId);
        existing.setStatus("ARCHIVED");
        fieldRepository.save(existing);
    }
}
