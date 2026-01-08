package com.nate.inventorymanagementsystemapi.mapper;

import com.nate.inventorymanagementsystemapi.dto.ProductDto;
import com.nate.inventorymanagementsystemapi.model.Product;
import com.nate.inventorymanagementsystemapi.model.User;

import java.time.Instant;

public class ProductMapper {

    public static Product toEntity(ProductDto productDto, User user){
        if(productDto == null){
            return null;
        }

        return new Product(
                productDto.getId(),
                productDto.getName(),
                productDto.getQuantity(),
                productDto.getPrice(),
                user,
                productDto.getCreatedAt()
        );
    }

    public static ProductDto toDto(Product product){
        if(product == null){
            return null;
        }

        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getQuantity(),
                product.getPrice(),
                product.getUser() != null ? product.getUser().getId() : null,
                product.getCreated() != null ? product.getCreated() : Instant.now()
        );
    }

    public static Product updateEntity(Product product, ProductDto dto){
        product.setName(dto.getName());
        product.setQuantity(dto.getQuantity());
        product.setPrice(dto.getPrice());
        return product;
    }
}
