package com.nate.inventorymanagementsystemapi.dto;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @NoArgsConstructor
@AllArgsConstructor @Setter
public class PostProduct {
    @NotBlank(message = "Product name cannot be empty")
    private String name;
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price must be greater than 0")
    private BigDecimal price;
}
