package com.nate.inventorymanagementsystemapi.service;

import com.nate.inventorymanagementsystemapi.dto.PostProduct;
import com.nate.inventorymanagementsystemapi.dto.ProductDto;

import java.util.List;

public interface IProductService {

    List<ProductDto> getAllUserProductsByUsername(String username);
    ProductDto addProduct(PostProduct product, String username);
    ProductDto getProduct(Long id);
    boolean deleteProduct(Long id,String username);
    ProductDto udpateProduct(Long id, ProductDto productUpdate);

}
