package com.example.orderservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class) // Required for @CreatedDate
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    String productCode;
    Integer quantity;
    private Double amount;
    private String status; // PENDING, PAID, CANCELLED
    // Constructor should NOT include createdAt
    public Order(Long id, String productCode, Integer quantity, Double amount, String status) {
        this.id = id;
        this.productCode = productCode;
        this.quantity = quantity;
        this.amount = amount;
        this.status = status;
    }
}


