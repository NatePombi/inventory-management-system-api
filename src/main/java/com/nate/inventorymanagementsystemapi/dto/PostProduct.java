package com.nate.inventorymanagementsystemapi.dto;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @AllArgsConstructor @Setter
public class PostProduct {
    @NotBlank
    private String name;
    @Min(value = 1)
    private int quantity;
    private BigDecimal price;
}
