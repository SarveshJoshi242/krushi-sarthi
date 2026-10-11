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
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String address;
    private String city;
    private String state;
    private String pincode;
}
