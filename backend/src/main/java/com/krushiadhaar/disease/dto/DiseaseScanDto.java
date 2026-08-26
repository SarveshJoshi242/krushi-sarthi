package com.krushiadhaar.disease.dto;
import lombok.Data;
import java.util.UUID;
@Data
public class DiseaseScanDto {
    private UUID id;
    private UUID cropCycleId;
    private String status;
    private String imageReference;
}
