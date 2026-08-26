package com.krushiadhaar.marketplace.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    private LocalDateTime paymentDeadline;
    @OneToMany(mappedBy = "order")
    private java.util.List<OrderItem> items = new java.util.ArrayList<>();
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "buyer_user_id", nullable = false)
    private UUID buyerUserId;

    @Builder.Default
    private String status = "PENDING";

    private BigDecimal subtotal;
    private BigDecimal totalAmount;
    
    @Builder.Default
    private String currency = "INR";

    @Column(name = "idempotency_key", nullable = false, unique = true)
    private UUID idempotencyKey;

    @Version
    private Long version;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
