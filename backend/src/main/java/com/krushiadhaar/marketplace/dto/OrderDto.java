package com.krushiadhaar.marketplace.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;
@Data
public class OrderDto {
    private UUID id;
    private UUID buyerUserId;
    private BigDecimal subtotal;
    private BigDecimal totalAmount;
    private String status;
}
