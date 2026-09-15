package com.krushiadhaar.farm.dto;
import lombok.Data;
import java.math.BigDecimal;
@Data
public class CreateFieldRequest {
    private String name;
    private BigDecimal area;
    private String soilType;
}
