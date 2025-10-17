package com.nate.inventorymanagementsystemapi.service;

import com.nate.inventorymanagementsystemapi.dto.PostProduct;
import com.nate.inventorymanagementsystemapi.dto.ProductDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IProductService {

    Page<ProductDto> getAllUserProductsByUsername(String username, int page, int size, String sortBy, String direction);
    ProductDto addProduct(PostProduct product, String username);
    ProductDto getProduct(Long id,String username);
    boolean deleteProduct(Long id,String username);
    ProductDto udpateProduct(Long id, ProductDto productUpdate,String username);

}
