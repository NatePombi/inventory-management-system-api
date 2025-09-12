package com.nate.inventorymanagementsystemapi.mapper;

import com.nate.inventorymanagementsystemapi.dto.ProductDto;
import com.nate.inventorymanagementsystemapi.model.Product;
import com.nate.inventorymanagementsystemapi.model.User;

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
                user
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
                product.getUser() != null ? product.getUser().getId() : null
        );
    }
}
