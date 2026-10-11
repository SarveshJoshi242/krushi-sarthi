package com.krushiadhaar.farm.dto;
import lombok.Data;
import java.math.BigDecimal;
@Data
public class UpdateFarmRequest {
    private String name;
    private BigDecimal totalArea;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String address;
    private String city;
    private String state;
    private String pincode;
}
