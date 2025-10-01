package com.nate.inventorymanagementsystemapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Product creation request")
public class PostProduct {
    @Schema(description = "Name of product", example = "Laptop")
    @NotBlank(message = "Product name cannot be empty")
    private String name;
    @Schema(description = "Quantity in stock", example = "10")
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price must be greater than 0")
    @Schema(description = "Price of the product", example = "1200.00")
    private BigDecimal price;
}
