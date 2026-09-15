package com.krushiadhaar.farm.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;
@Data
public class FarmDto {
    private UUID id;
    private String name;
    private BigDecimal totalArea;
    private String status;
}
