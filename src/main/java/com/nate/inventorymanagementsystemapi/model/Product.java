package com.nate.inventorymanagementsystemapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Name is required")
    private String name;
    @Min(1)
    private int quantity;
    @DecimalMin("1")
    private BigDecimal price;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(nullable = false, updatable = false)
    private Instant created;
}
