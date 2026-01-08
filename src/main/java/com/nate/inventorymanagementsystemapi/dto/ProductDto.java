package com.nate.inventorymanagementsystemapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Setter @Getter @AllArgsConstructor @NoArgsConstructor
public class ProductDto {
    private Long id;
    private String name;
    private int quantity;
    private BigDecimal price;
    private Long userId;
    private Instant createdAt;
}
