package com.krushiadhaar.farm.dto;
import lombok.Data;
import java.math.BigDecimal;
@Data
public class UpdateFarmRequest {
    private String name;
    private BigDecimal totalArea;
}
