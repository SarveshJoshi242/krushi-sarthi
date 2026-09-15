package com.krushiadhaar.farm.dto;
import lombok.Data;
import java.math.BigDecimal;
@Data
public class CreateFarmRequest {
    private String name;
    private BigDecimal totalArea;
}
