package com.nate.inventorymanagementsystemapi.dto;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @AllArgsConstructor @Setter
public class PostProduct {
    private String name;
    private int quantity;
    private BigDecimal price;
}
