package com.krushiadhaar.farm.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;
@Data
public class FieldDto {
    private UUID id;
    private String name;
    private BigDecimal area;
    private String soilType;
    private String status;
}
