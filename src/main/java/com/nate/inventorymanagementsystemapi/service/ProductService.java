package com.nate.inventorymanagementsystemapi.service;

import com.nate.inventorymanagementsystemapi.dto.PostProduct;
import com.nate.inventorymanagementsystemapi.dto.ProductDto;
import com.nate.inventorymanagementsystemapi.exception.ProductNotFoundException;
import com.nate.inventorymanagementsystemapi.exception.UserNotFoundException;
import com.nate.inventorymanagementsystemapi.mapper.ProductMapper;
import com.nate.inventorymanagementsystemapi.model.Product;
import com.nate.inventorymanagementsystemapi.model.Role;
import com.nate.inventorymanagementsystemapi.model.User;
import com.nate.inventorymanagementsystemapi.repository.ProductRepository;
import com.nate.inventorymanagementsystemapi.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class ProductService implements IProductService {

    private final ProductRepository repo;
    private final UserRepository repoU;


    @Override
    public List<ProductDto> getAllUserProductsByUsername(String username) {
       User user = repoU.findByUsername(username).orElseThrow(()-> new UserNotFoundException(username));

       if (user.getRole().equals(Role.ADMIN)) {
           return repo.findAll().stream()
                   .map(ProductMapper::toDto)
                   .toList();
       }


        return repo.findAll().stream()
                .filter(m-> m.getUser().getUsername().equals(username))
                .map(ProductMapper::toDto)
                .toList();
    }

    @Override
    public ProductDto addProduct(PostProduct product, String username) {
        User user = repoU.findByUsername(username)
                .orElseThrow(()-> new UserNotFoundException(username));

        ProductDto dto = new ProductDto();
        dto.setName(product.getName());
        dto.setQuantity(product.getQuantity());
        dto.setPrice(product.getPrice());

        Product product1 = ProductMapper.toEntity(dto,user);
        Product saved = repo.save(product1);

        return ProductMapper.toDto(saved);
    }

    @Override
    public ProductDto getProduct(Long id,String username) {
        User user = repoU.findByUsername(username)
                .orElseThrow(()->new UserNotFoundException(username));

        Product product = repo.findById(id)
                .orElseThrow(()-> new ProductNotFoundException(id));

        if(!product.getUser().getId().equals(user.getId()) && !user.getRole().equals(Role.ADMIN)){
            throw new AccessDeniedException("Not Authorized");
        }

        return ProductMapper.toDto(product);
    }

    @Override
    public boolean deleteProduct(Long id, String username) {
        User user = repoU.findByUsername(username)
                .orElseThrow(()-> new UserNotFoundException(username));

        Product product = repo.findById(id)
                .orElseThrow(()-> new ProductNotFoundException(id));


        if (user.getRole().equals(Role.ADMIN)) {
            repo.delete(product);
            return true;
        } else if (product.getUser().getId().equals(user.getId())) {
            repo.delete(product);
            return true;
        }


        throw new AccessDeniedException("Dont have Authorization");
    }

    @Override
    public ProductDto udpateProduct(Long id, ProductDto productUpdate) {


        Product product = repo.findById(id)
                .orElseThrow(()-> new ProductNotFoundException(id));



        Product updateProd = ProductMapper.updateEntity(product,productUpdate);

        repo.save(updateProd);
        return ProductMapper.toDto(product);
    }
}
